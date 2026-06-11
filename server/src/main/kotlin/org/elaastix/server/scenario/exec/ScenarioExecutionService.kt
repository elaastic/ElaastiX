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
import org.elaastix.commons.orNotFound
import org.elaastix.commons.platform.debt.SciconumTechDebt
import org.elaastix.commons.schedule
import org.elaastix.commons.security.RequiresRole
import org.elaastix.commons.security.Role
import org.elaastix.commons.validate
import org.elaastix.server.scenario.SciconumScenario
import org.elaastix.server.scenario.exec.entities.SciconumSessionEntity
import org.elaastix.server.scenario.exec.repositories.SciconumLearnerSessionRepository
import org.elaastix.server.scenario.exec.repositories.SciconumSessionRepository
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Component
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
	private val taskScheduler: TaskScheduler,
	private val transactionTemplate: TransactionTemplate,
	private val sciconumSessionRepository: SciconumSessionRepository,
	private val sciconumLearnerSessionRepository: SciconumLearnerSessionRepository,
	private val clock: Clock,
) {
	companion object {
		private val LOGGER = LogFactory.getLog(ScenarioExecutionService::class.java)
	}

	private val sessionFutureMap: MutableMap<Uuid, Future<*>> = mutableMapOf()

	/** Conditionally loaded component dealing with session execution restoration. */
	@Component
	@Profile("!openapi")
	class ScenarioExecutionRestoreListener(
		private val sciconumSessionRepository: SciconumSessionRepository,
		private val executionService: ScenarioExecutionService,
		private val clock: Clock,
	) {
		/** Event listener called by Spring on application start. */
		@Transactional
		@EventListener(ApplicationStartedEvent::class)
		fun restoreRunningSequences() {
			val now = clock.now()
			for (session in sciconumSessionRepository.findAllByNextPhaseAtNotNullAndPausedAtNull()) {
				session.nextPhaseAt?.let {
					when {
						it < now -> {
							val lag = (now - it).toInt(DurationUnit.SECONDS)
							LOGGER.warn("Ticking session ${session.id}, was supposed to tick at $it ($lag seconds behind!)")
							executionService.tickSession(session)
						}

						else -> {
							LOGGER.info("Resuming execution of session ${session.id}")
							executionService.scheduleSessionTick(session, it)
						}
					}
				}
			}
		}
	}

	@Transactional
	@RequiresRole(Role.WRITER)
	fun startSequenceById(sessionId: Uuid) {
		val session = sciconumSessionRepository.findById(sessionId).orNotFound()
		startSequence(session)
	}

	@Transactional
	@RequiresRole(Role.WRITER)
	fun pauseSequenceById(sessionId: Uuid) {
		val session = sciconumSessionRepository.findById(sessionId).orNotFound()
		pauseSequence(session)
	}

	@Transactional
	@RequiresRole(Role.WRITER)
	fun resumeSequenceById(sessionId: Uuid) {
		val session = sciconumSessionRepository.findById(sessionId).orNotFound()
		resumeSequence(session)
	}

	@Transactional
	@RequiresRole(Role.WRITER)
	fun resetSequenceById(sessionId: Uuid) {
		val session = sciconumSessionRepository.findById(sessionId).orNotFound()
		resetSequence(session)
	}

	@Transactional
	fun startSequence(session: SciconumSessionEntity) {
		validate(session.phase == Phase.PENDING) { "The session is already in progress." }
		ExecutionTask(session.id).run()
	}

	@Transactional
	fun pauseSequence(session: SciconumSessionEntity) {
		validate(session.phase != Phase.PENDING) { "The session has not started." }
		validate(session.phase != Phase.END) { "The session has terminated." }
		validate(session.pausedAt == null) { "The session has already been paused." }

		session.pausedAt = clock.now()
		descheduleSessionTick(session)

		// TODO: WS event
	}

	@Transactional
	fun resumeSequence(session: SciconumSessionEntity) {
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
	fun resetSequence(session: SciconumSessionEntity) {
		validate(session.phase != Phase.PENDING) { "The session has not started." }

		// TODO: delete all produced content
		descheduleSessionTick(session)
		session.apply {
			phase = Phase.PENDING
			pausedAt = null
			nextPhaseAt = null
			currentQuestion = 0u
		}

		// TODO: WS event
	}

	private fun scheduleSessionTick(session: SciconumSessionEntity, nextTick: Instant) {
		val future = taskScheduler.schedule(ExecutionTask(session.id), nextTick)
		sessionFutureMap.put(session.id, future)?.cancel(true)
	}

	private fun descheduleSessionTick(session: SciconumSessionEntity) {
		sessionFutureMap.remove(session.id)?.cancel(true)
	}

	@Suppress("CyclomaticComplexMethod") // The logic is just a big state machine
	private fun tickSession(session: SciconumSessionEntity): Instant? {
		val currentPhase = session.phase
		val scenario = session.sequence.sciconumScenario

		if (LOGGER.isDebugEnabled) {
			val now = clock.now()
			val delta = session.nextPhaseAt?.let { (now - it).toInt(DurationUnit.SECONDS) } ?: 0
			LOGGER.debug(
				"Ticking session ${session.id} (" +
					"Scenario: $scenario; " +
					"Current phase: $currentPhase; " +
					"Round: ${session.currentQuestion}; " +
					"Tick delay: $delta seconds)",
			)
		}

		val constants = when (scenario) {
			SciconumScenario.CONTROL -> Control
			SciconumScenario.PEER_ASSESSMENT -> Assessment
			SciconumScenario.PEER_DEBATE -> Debate
		}

		return when (currentPhase) {
			Phase.PENDING ->
				updateSession(session, Phase.QUESTION, constants.ANSWER_PHASE_DURATION)

			Phase.QUESTION ->
				when (scenario) {
					SciconumScenario.CONTROL ->
						updateSession(session, Phase.FEEDBACK, constants.FEEDBACK_PHASE_DURATION)

					SciconumScenario.PEER_ASSESSMENT ->
						startPeerAssessmentPhase(session)

					SciconumScenario.PEER_DEBATE ->
						startPeerDebatePhase(session)
				}

			Phase.PEER ->
				updateSession(session, Phase.REVISE, constants.REVISE_PHASE_DURATION)

			Phase.REVISE ->
				updateSession(session, Phase.FEEDBACK, constants.FEEDBACK_PHASE_DURATION)

			Phase.FEEDBACK -> {
				when (++session.currentQuestion) {
					session.sequence.sciconumQuestions.size.toUInt() ->
						updateSession(session, Phase.END, null)

					else ->
						updateSession(session, Phase.QUESTION, constants.ANSWER_PHASE_DURATION)
				}
			}

			Phase.END -> error("Ticking an ended session?!")
		}
	}

	private fun startPeerAssessmentPhase(session: SciconumSessionEntity): Instant? {
		// TODO: Assign answers
		return updateSession(session, Phase.PEER, Assessment.PEER_PHASE_DURATION)
	}

	private fun startPeerDebatePhase(session: SciconumSessionEntity): Instant? {
		// TODO: Assign peers
		return updateSession(session, Phase.PEER, Debate.PEER_PHASE_DURATION)
	}

	private fun updateSession(session: SciconumSessionEntity, phase: Phase, nextTickIn: Duration?): Instant? {
		val nextTick = nextTickIn?.let { clock.now().plus(nextTickIn) }
		LOGGER.trace("Transitioning session ${session.id} (${session.phase} -> $phase)")

		session.phase = phase
		session.nextPhaseAt = nextTick
		sciconumLearnerSessionRepository.transitionAllLearnerSessionsOfSessionTo(session, phase, nextTick)

		// TODO: WS event

		return nextTick
	}

	private inner class ExecutionTask(private val sessionId: Uuid) : Runnable {
		override fun run() {
			transactionTemplate.execute {
				val session = checkNotNull(sciconumSessionRepository.findByIdOrNull(sessionId))
				tickSession(session)
			}?.let {
				if (LOGGER.isDebugEnabled) {
					val now = clock.now()
					val delta = (it - now).toInt(DurationUnit.SECONDS)
					LOGGER.debug("Scheduling next tick of session $sessionId at $it (in $delta seconds)")
				}
				taskScheduler.schedule(this, it)
			}
		}
	}
}
