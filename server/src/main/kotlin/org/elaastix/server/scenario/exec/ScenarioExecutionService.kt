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
import org.elaastix.server.scenario.SciconumScenario
import org.elaastix.server.scenario.exec.entities.SciconumScenarioSessionEntity
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
import kotlin.time.Instant
import org.elaastix.server.scenario.exec.SciconumScenarioExecutionPhase as Phase

/**
 * Service implementing the execution flow of the sequences.
 * Caution: it assumes there is only one instance of the app ever running (which is going to be the case for now).
 */
@Service
@SciconumTechDebt
@Suppress("TooManyFunctions")
class ScenarioExecutionService(
	private val clock: Clock,
	private val taskScheduler: TaskScheduler,
	private val transactionTemplate: TransactionTemplate,
	private val sciconumScenarioSessionRepository: SciconumScenarioSessionRepository,
	private val sciconumLearnerSessionRepository: SciconumLearnerSessionRepository,
	private val gradingService: ScenarioGradingService,
	private val peeringService: ScenarioPeeringService,
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
						scheduleSessionTick(session, it)
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
		ExecutionTask(session.id).run()
	}

	@Transactional
	@RequiresRole(Role.WRITER)
	fun pauseSequenceScenarioSessionById(scenarioSessionId: Uuid) {
		val session = sciconumScenarioSessionRepository.findById(scenarioSessionId).orElseNotFound()

		validate(session.phase != Phase.PENDING) { "The session has not started." }
		validate(session.phase != Phase.END) { "The session has terminated." }
		validate(session.pausedAt == null) { "The session has already been paused." }

		session.pausedAt = clock.now()
		descheduleSessionTick(session)

		// TODO: WS event
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
			scheduleSessionTick(session, nextTick)
		} ?: LOGGER.warn("Unpaused session ${session.id}, but it didn't have a next scheduled tick.")

		// TODO: WS event
	}

	@Transactional
	@RequiresRole(Role.WRITER)
	fun resetSequenceScenarioSessionById(scenarioSessionId: Uuid) {
		val session = sciconumScenarioSessionRepository.findById(scenarioSessionId).orElseNotFound()

		validate(session.phase != Phase.PENDING) { "The session has not started." }

		// TODO: delete all produced content
		descheduleSessionTick(session)
		session.apply {
			phase = Phase.PENDING
			pausedAt = null
			nextPhaseAt = null
			currentRound = 0u
		}

		// TODO: WS event
	}

	private fun scheduleSessionTick(session: SciconumScenarioSessionEntity, nextTick: Instant) =
		scheduleSessionTick(ExecutionTask(session.id), nextTick)

	private fun scheduleSessionTick(task: ExecutionTask, nextTick: Instant) {
		if (LOGGER.isDebugEnabled) {
			val now = clock.now()
			val delta = (nextTick - now).toInt(DurationUnit.SECONDS)
			LOGGER.debug("Scheduling next tick of session ${task.scenarioSessionId} at $nextTick (in $delta seconds)")
		}

		val future = taskScheduler.schedule(task, nextTick)
		sessionFutureMap.put(task.scenarioSessionId, future)?.cancel(true)
	}

	private fun descheduleSessionTick(session: SciconumScenarioSessionEntity) {
		sessionFutureMap.remove(session.id)?.let {
			LOGGER.debug("De-scheduling next tick of session ${session.id}")
			it.cancel(true)
		}
	}

	@Suppress("CyclomaticComplexMethod") // The logic is just a big state machine
	private fun tickScenarioSession(session: SciconumScenarioSessionEntity): Instant? {
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
			SciconumScenario.CONTROL -> ScnConstants.Control
			SciconumScenario.PEER_ASSESSMENT -> ScnConstants.Assessment
			SciconumScenario.PEER_DEBATE -> ScnConstants.Debate
		}

		return when (currentPhase) {
			Phase.PENDING -> updateSession(session, Phase.QUESTION, constants.ANSWER_PHASE_DURATION)

			Phase.QUESTION -> {
				val responses = gradingService.gradeResponsesOfSession(session)

				when (scenario) {
					SciconumScenario.CONTROL ->
						updateSession(session, Phase.FEEDBACK, constants.FEEDBACK_PHASE_DURATION)

					SciconumScenario.PEER_ASSESSMENT ->
						peeringService.assignPeerResponses(session, responses)
							?.let { updateSession(session, Phase.PEER, ScnConstants.Assessment.PEER_PHASE_DURATION) }
							?: updateSession(session, Phase.FEEDBACK, ScnConstants.Control.FEEDBACK_PHASE_DURATION)

					SciconumScenario.PEER_DEBATE ->
						peeringService.assignPeerChatters(session, responses)
							?.let { updateSession(session, Phase.PEER, ScnConstants.Debate.PEER_PHASE_DURATION) }
							?: updateSession(session, Phase.FEEDBACK, ScnConstants.Control.FEEDBACK_PHASE_DURATION)
				}
			}

			Phase.PEER -> updateSession(session, Phase.REVISE, constants.REVISE_PHASE_DURATION)

			Phase.REVISE -> updateSession(session, Phase.FEEDBACK, constants.FEEDBACK_PHASE_DURATION)

			Phase.FEEDBACK -> {
				when (++session.currentRound) {
					session.sequence.sciconumQuestions.size.toUInt() ->
						updateSession(session, Phase.END, null)

					else ->
						updateSession(session, Phase.QUESTION, constants.ANSWER_PHASE_DURATION)
				}
			}

			Phase.END -> error("Ticking an ended session?!")
		}
	}

	private fun updateSession(session: SciconumScenarioSessionEntity, phase: Phase, nextTickIn: Duration?): Instant? {
		val nextTick = nextTickIn?.let { clock.now().plus(nextTickIn) }
		LOGGER.trace("Transitioning session ${session.id} (${session.phase} -> $phase)")

		session.phase = phase
		session.nextPhaseAt = nextTick
		sciconumLearnerSessionRepository.transitionAllLearnerSessionsOfSessionTo(session, phase, nextTick)

		// TODO: WS event

		return nextTick
	}

	private inner class ExecutionTask(val scenarioSessionId: Uuid) : Runnable {
		override fun run() {
			transactionTemplate.execute {
				val session = checkNotNull(sciconumScenarioSessionRepository.findByIdOrNull(scenarioSessionId))
				tickScenarioSession(session)
			}?.let {
				scheduleSessionTick(this, it)
			}
		}
	}
}
