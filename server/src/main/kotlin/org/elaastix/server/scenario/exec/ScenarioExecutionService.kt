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
import org.elaastix.server.scenario.exec.repositories.SciconumLearnerSessionRepository
import org.elaastix.server.scenario.exec.repositories.SciconumScenarioSessionRepository
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import java.util.concurrent.Future
import kotlin.time.Clock
import kotlin.time.Duration
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
	private val webSocketEventPublisher: WebSocketEventPublisher,
	private val clock: Clock,
) {
	companion object {
		private val LOGGER = LogFactory.getLog(ScenarioExecutionService::class.java)
	}

	private val sessionFutureMap: MutableMap<Uuid, Future<*>> = mutableMapOf()

	/**
	 * Resumes execution of scenario sessions, in case the server has been restarted or crashed.
	 */
	@Transactional
	fun restoreRunningScenarioSessions() {
		val now = clock.now()
		val ongoingSessions = sciconumScenarioSessionRepository.findAllByNextPhaseAtNotNullAndPausedAtNull()
		for (session in ongoingSessions) {
			session.nextPhaseAt?.let {
				when {
					it < now -> {
						val lag = (now - it).toInt(DurationUnit.SECONDS)
						LOGGER.warn(
							"Ticking scenario session ${session.id}, " +
								"was supposed to tick at $it ($lag seconds behind!)",
						)
						tickScenarioSession(session)
					}

					else -> {
						LOGGER.info("Resuming execution of scenario session ${session.id}")
						session.scheduleTick()
					}
				}
			}
		}
	}

	@Transactional
	@RequiresRole(Role.WRITER)
	fun startSequenceScenarioSessionById(scenarioSessionId: Uuid) {
		val session = sciconumScenarioSessionRepository.findById(scenarioSessionId).orElseNotFound()
		validate(session.phase == Phase.PENDING) { "The session is already in progress." }
		ExecutionTickTask(session.id).run()
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

		// TODO: delete all produced content
		session.apply {
			phase = Phase.PENDING
			pausedAt = null
			nextPhaseAt = null
			currentRound = 0u
		}

		session.dispatchTransition()
		session.scheduleTick()
	}

	@Suppress("CyclomaticComplexMethod") // The logic is just a big state machine
	private fun tickScenarioSession(session: ScenarioSessionEntity) {
		val currentPhase = session.phase
		val scenario = session.sequence.sciconumScenario

		if (LOGGER.isDebugEnabled) {
			val now = clock.now()
			val delta = session.nextPhaseAt?.let { (now - it).toInt(DurationUnit.SECONDS) } ?: 0
			LOGGER.debug(
				"Ticking session ${session.id} (" +
					"Scenario: $scenario; " +
					"Current phase: $currentPhase; " +
					"Round: ${session.currentRound}; " +
					"Tick delay: $delta seconds)",
			)
		}

		val constants = when (scenario) {
			SciconumScenario.CONTROL -> Control
			SciconumScenario.PEER_ASSESSMENT -> Assessment
			SciconumScenario.PEER_DEBATE -> Debate
		}

		when (currentPhase) {
			Phase.PENDING ->
				updateScenarioSession(session, Phase.QUESTION, constants.ANSWER_PHASE_DURATION)

			Phase.QUESTION ->
				when (scenario) {
					SciconumScenario.CONTROL ->
						updateScenarioSession(session, Phase.FEEDBACK, constants.FEEDBACK_PHASE_DURATION)

					SciconumScenario.PEER_ASSESSMENT ->
						startPeerAssessmentPhase(session)

					SciconumScenario.PEER_DEBATE ->
						startPeerDebatePhase(session)
				}

			Phase.PEER ->
				updateScenarioSession(session, Phase.REVISE, constants.REVISE_PHASE_DURATION)

			Phase.REVISE ->
				updateScenarioSession(session, Phase.FEEDBACK, constants.FEEDBACK_PHASE_DURATION)

			Phase.FEEDBACK -> {
				when (++session.currentRound) {
					session.sequence.sciconumQuestions.size.toUInt() ->
						updateScenarioSession(session, Phase.END, null)

					else ->
						updateScenarioSession(session, Phase.QUESTION, constants.ANSWER_PHASE_DURATION)
				}
			}

			Phase.END -> error("Ticking an ended session?!")
		}

		session.dispatchTransition()
	}

	private fun startPeerAssessmentPhase(session: ScenarioSessionEntity) {
		// TODO: Assign answers
		updateScenarioSession(session, Phase.PEER, Assessment.PEER_PHASE_DURATION)
	}

	private fun startPeerDebatePhase(session: ScenarioSessionEntity) {
		// TODO: Assign peers
		updateScenarioSession(session, Phase.PEER, Debate.PEER_PHASE_DURATION)
	}

	private fun updateScenarioSession(session: ScenarioSessionEntity, phase: Phase, nextTickIn: Duration?) {
		val nextTick = nextTickIn?.let { clock.now().plus(nextTickIn) }
		LOGGER.trace("Transitioning session ${session.id} (${session.phase} -> $phase)")

		session.phase = phase
		session.nextPhaseAt = nextTick
		sciconumLearnerSessionRepository.transitionAllLearnerSessionsOfSessionTo(session, phase, nextTick)
	}

	private fun ScenarioSessionEntity.dispatchTransition() {
		val duration = nextPhaseAt?.let { it - clock.now() }
		val message = ScenarioTransitionMessage(phase, pausedAt != null, duration)
		webSocketEventPublisher.publishPayload(id, message)
	}

	private fun ScenarioSessionEntity.scheduleTick() = scheduleTick(ExecutionTickTask(id))
	private fun ScenarioSessionEntity.scheduleTick(task: ExecutionTickTask) {
		nextPhaseAt?.let {
			if (LOGGER.isDebugEnabled) {
				val now = clock.now()
				val delta = (it - now).toInt(DurationUnit.SECONDS)
				LOGGER.debug("Scheduling next tick of session ${task.scenarioSessionId} at $it (in $delta seconds)")
			}

			val future = taskScheduler.schedule(task, it)
			sessionFutureMap.put(task.scenarioSessionId, future)?.cancel(true)
		}
	}

	private fun ScenarioSessionEntity.deschedule() {
		sessionFutureMap.remove(id)?.let {
			LOGGER.debug("De-scheduling next tick of session $it")
			it.cancel(true)
		}
	}

	private inner class ExecutionTickTask(val scenarioSessionId: Uuid) : Runnable {
		override fun run() {
			transactionTemplate.execute {
				val session = checkNotNull(sciconumScenarioSessionRepository.findByIdOrNull(scenarioSessionId))
				tickScenarioSession(session)
				session.scheduleTick(this)
			}
		}
	}
}
