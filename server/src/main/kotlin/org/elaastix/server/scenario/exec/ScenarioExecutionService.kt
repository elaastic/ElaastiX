/*
 * Elaastic / ElaastiX - formative assessment system
 * Copyright (C) 2019  Université de Toulouse and Université Toulouse Capitole.
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.elaastix.server.scenario.exec

import org.apache.commons.logging.LogFactory
import org.elaastix.commons.data.Uuid
import org.elaastix.commons.orElseNotFound
import org.elaastix.commons.platform.debt.SciconumTechDebt
import org.elaastix.commons.schedule
import org.elaastix.commons.security.RequiresRole
import org.elaastix.commons.security.Role
import org.elaastix.commons.validate
import org.elaastix.commons.ws.WebSocketEventPublisher
import org.elaastix.server.scenario.SciconumScenario
import org.elaastix.server.scenario.exec.dto.SciconumScenarioPhaseDto
import org.elaastix.server.scenario.exec.repositories.SciconumLearnerSessionRepository
import org.elaastix.server.scenario.exec.repositories.SciconumScenarioSessionRepository
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import java.util.concurrent.Future
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import org.elaastix.server.scenario.exec.SciconumScenarioExecutionPhase as Phase
import org.elaastix.server.scenario.exec.entities.SciconumScenarioSessionEntity as ScenarioSessionEntity

/**
 * Service implementing the execution flow of the sequences.
 * Caution: it assumes there is only one instance of the app ever running (which is going to be the case for now).
 */
@Service
@SciconumTechDebt
@Suppress("TooManyFunctions")
class ScenarioExecutionService(
	private val taskScheduler: TaskScheduler,
	private val transactionTemplate: TransactionTemplate,
	private val sciconumScenarioSessionRepository: SciconumScenarioSessionRepository,
	private val sciconumLearnerSessionRepository: SciconumLearnerSessionRepository,
	private val webSocketSessionBinderService: WebSocketSessionBinderService,
	private val webSocketEventPublisher: WebSocketEventPublisher,
	private val clock: Clock,
) {
	companion object {
		private val LOGGER = LogFactory.getLog(ScenarioExecutionService::class.java)
	}

	private val sessionFutureMap: MutableMap<Uuid, Future<*>> = mutableMapOf()

	/**
	 * Resumes execution of scenario sessions, in case the server has been restarted or crashed.
	 *
	 * Caution: this method does not perform WebSocket session binding, as it runs under the assumption it only runs
	 * after the server experienced total availability loss. In these circumstances, clients reconnecting to the server
	 * will be bound as expected at connect time.
	 *
	 * In other words, a session that is to be restored by this recovery mechanism is a session that is already in
	 * progress, and clients connecting to the real-time endpoint automatically get bound to them at connect time.
	 */
	@Async
	@Transactional
	fun restoreRunningScenarioSessions() {
		val ongoingSessions = sciconumScenarioSessionRepository.findAllByNextPhaseAtNotNullAndPausedAtNull()
		for (session in ongoingSessions) {
			session.nextPhaseAt?.let {
				LOGGER.info("Resuming execution of scenario session ${session.id}")
				session.scheduleTick()
			}
		}
	}

	@Transactional
	@RequiresRole(Role.WRITER)
	fun startSequenceScenarioSessionById(scenarioSessionId: Uuid) {
		val session = sciconumScenarioSessionRepository.findById(scenarioSessionId).orElseNotFound()
		validate(session.phase == Phase.PENDING) { "The session is already in progress." }

		webSocketSessionBinderService.bindBroadcastScopesForSciconumSequenceSession(session)
		session.tick()
	}

	@Transactional
	@RequiresRole(Role.WRITER)
	fun pauseSequenceScenarioSessionById(scenarioSessionId: Uuid) {
		val session = sciconumScenarioSessionRepository.findById(scenarioSessionId).orElseNotFound()

		validate(session.phase != Phase.PENDING) { "The session has not started." }
		validate(session.phase != Phase.END) { "The session has terminated." }
		validate(session.pausedAt == null) { "The session has already been paused." }

		session.pausedAt = clock.now()
		session.dispatchTransition()
		session.deschedule()
	}

	@Transactional
	@RequiresRole(Role.WRITER)
	fun resumeSequenceScenarioSessionById(scenarioSessionId: Uuid) {
		val session = sciconumScenarioSessionRepository.findById(scenarioSessionId).orElseNotFound()

		val suspendedAt = session.pausedAt
		validate(session.phase != Phase.PENDING) { "The session has not started." }
		validate(session.phase != Phase.END) { "The session has terminated." }
		validate(suspendedAt != null) { "The session has not been paused." }

		val suspendedFor = clock.now() - suspendedAt
		session.pausedAt = null
		session.nextPhaseAt?.let {
			val nextTick = it + suspendedFor
			session.nextPhaseAt = nextTick
		} ?: LOGGER.warn("Unpaused session ${session.id}, but it didn't have a next scheduled tick.")

		session.dispatchTransition()
		session.scheduleTick()
	}

	@Transactional
	@RequiresRole(Role.WRITER)
	fun resetSequenceScenarioSessionById(scenarioSessionId: Uuid) {
		val session = sciconumScenarioSessionRepository.findById(scenarioSessionId).orElseNotFound()
		validate(session.phase != Phase.PENDING) { "The session has not started." }
		validate(session.phase != Phase.END) { "The session has already ended." }

		// TODO: delete all produced content
		session.apply {
			phase = Phase.PENDING
			pausedAt = null
			nextPhaseAt = null
			currentRound = 0u
		}

		webSocketSessionBinderService.freeBroadcastScopesOfSession(session)
		session.dispatchTransition()
		session.scheduleTick()
	}

	@Suppress("UnusedParameter")
	private fun preparePeerAssessmentPhase(session: ScenarioSessionEntity) {
		// TODO: Assign answers
	}

	@Suppress("UnusedParameter")
	private fun preparePeerDebatePhase(session: ScenarioSessionEntity) {
		// TODO: Assign peers
	}

	@Suppress("CyclomaticComplexMethod") // The logic is just a big state machine
	private final fun ScenarioSessionEntity.tick() {
		check(pausedAt == null) { "Ticking a paused session?!" }
		logTick()

		val scenario = sequence.sciconumScenario
		val constants = when (scenario) {
			SciconumScenario.CONTROL -> Control
			SciconumScenario.PEER_ASSESSMENT -> Assessment
			SciconumScenario.PEER_DEBATE -> Debate
		}

		when (phase) {
			Phase.PENDING -> transition(Phase.QUESTION, constants.ANSWER_PHASE_DURATION)

			Phase.QUESTION ->
				when (scenario) {
					SciconumScenario.CONTROL ->
						transition(Phase.FEEDBACK, constants.FEEDBACK_PHASE_DURATION)

					SciconumScenario.PEER_ASSESSMENT -> {
						preparePeerAssessmentPhase(this)
						transition(Phase.PEER, Assessment.PEER_PHASE_DURATION)
					}

					SciconumScenario.PEER_DEBATE -> {
						preparePeerDebatePhase(this)
						transition(Phase.PEER, Debate.PEER_PHASE_DURATION)
					}
				}

			Phase.PEER -> transition(Phase.REVISE, constants.REVISE_PHASE_DURATION)

			Phase.REVISE -> transition(Phase.FEEDBACK, constants.FEEDBACK_PHASE_DURATION)

			Phase.FEEDBACK -> {
				when (++currentRound) {
					sequence.sciconumQuestions.size.toUInt() -> {
						webSocketSessionBinderService.freeBroadcastScopesOfSession(this)
						transition(Phase.END, null)
					}

					else -> transition(Phase.QUESTION, constants.ANSWER_PHASE_DURATION)
				}
			}

			Phase.END -> error("Ticking an ended session?!")
		}

		scheduleTick()
	}

	private final fun ScenarioSessionEntity.transition(nextPhase: Phase, nextTickIn: Duration?) {
		val nextTick = nextTickIn?.let { clock.now().plus(nextTickIn) }
		LOGGER.trace("Transitioning session $id ($phase -> $nextPhase)")

		phase = nextPhase
		nextPhaseAt = nextTick
		sciconumLearnerSessionRepository.transitionAllLearnerSessionsOfSessionTo(this, phase, nextTick)
		dispatchTransition()
	}

	private final fun ScenarioSessionEntity.dispatchTransition() {
		val duration = nextPhaseAt?.let { it - clock.now() }
		val message = ScenarioTransitionMessage(
			phase,
			when {
				pausedAt != null -> ScenarioTransitionMessage.State.PAUSED
				else -> ScenarioTransitionMessage.State.RUNNING
			},
			duration,
		)

		webSocketEventPublisher.publishPayload(id, message)
	}

	private final fun ScenarioSessionEntity.scheduleTick() {
		nextPhaseAt?.let {
			if (LOGGER.isDebugEnabled) {
				val now = clock.now()
				val delta = (it - now).toInt(DurationUnit.SECONDS)
				LOGGER.debug("Scheduling next tick of session $id at $it (in $delta seconds)")
			}

			val future = taskScheduler.schedule(SessionTickTask(id), it)
			sessionFutureMap.put(id, future)?.cancel(true)
		}
	}

	private final fun ScenarioSessionEntity.deschedule() {
		sessionFutureMap.remove(id)?.let {
			LOGGER.debug("De-scheduling next tick of session $it")
			it.cancel(true)
		}
	}

	private final fun ScenarioSessionEntity.logTick() {
		when (val scheduledAt = nextPhaseAt) {
			null -> {
				LOGGER.debug(
					"Ticking session $id (" +
						"Scenario: ${sequence.sciconumScenario}; " +
						"Current phase: $phase; " +
						"Round: $currentRound)",
				)
			}

			else -> {
				val delta = clock.now() - scheduledAt
				val deltaStr = delta.toString(DurationUnit.SECONDS, 2)

				if (delta > 1.seconds) {
					LOGGER.warn(
						"LAG: Scenario session $id was supposed to tick at $scheduledAt ($deltaStr seconds behind!)",
					)
				}

				LOGGER.debug(
					"Ticking session $id (" +
						"Scenario: ${sequence.sciconumScenario}; " +
						"Current phase: $phase; " +
						"Round: $currentRound; " +
						"Tick delay: $delta seconds)",
				)
			}
		}
	}

	fun getSciconumScenarioStateById(scenarioSessionId: Uuid): SciconumScenarioPhaseDto =
		SciconumScenarioPhaseDto.fromEntity(
			sciconumScenarioSessionRepository.findById(scenarioSessionId).orElseNotFound(),
		)

	private inner class SessionTickTask(val scenarioSessionId: Uuid) : Runnable {
		override fun run() {
			transactionTemplate.execute {
				val session = checkNotNull(sciconumScenarioSessionRepository.findByIdOrNull(scenarioSessionId))
				session.tick()
			}
		}
	}
}
