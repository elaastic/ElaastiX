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
import org.elaastix.server.scenario.SciconumScenario
import org.elaastix.server.scenario.exec.entities.SciconumScenarioSessionEntity
import org.elaastix.server.scenario.exec.repositories.SciconumLearnerSessionRepository
import org.elaastix.server.scenario.exec.repositories.SciconumScenarioSessionRepository
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
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
class ScenarioExecutionService(
	private val taskScheduler: TaskScheduler,
	private val transactionTemplate: TransactionTemplate,
	private val sciconumScenarioSessionRepository: SciconumScenarioSessionRepository,
	private val sciconumLearnerSessionRepository: SciconumLearnerSessionRepository,
	private val clock: Clock,
) {
	companion object {
		private val LOGGER = LogFactory.getLog(ScenarioExecutionService::class.java)
	}

	/**
	 * Resumes execution of sessions, in case the server has been restarted or crashed.
	 */
	@Transactional
	fun restoreRunningSequences() {
		val now = clock.now()
		val ongoingSessions = sciconumScenarioSessionRepository.findAllByNextPhaseAtNotNull()
		for (session in ongoingSessions) {
			session.nextPhaseAt?.let {
				when {
					it < now -> {
						val lag = (now - it).toInt(DurationUnit.SECONDS)
						LOGGER.warn("Ticking session ${session.id}, was supposed to tick at $it ($lag seconds behind!)")
						tickSession(session)
					}

					else -> {
						LOGGER.info("Resuming execution of session ${session.id}")
						taskScheduler.schedule(ExecutionTask(session.id), it)
					}
				}
			}
		}
	}

	@Transactional
	fun startSequenceById(sessionId: Uuid) {
		val session = sciconumScenarioSessionRepository.findById(sessionId).orElseNotFound()
		startSequence(session)
	}

	@Transactional
	fun startSequence(session: SciconumScenarioSessionEntity) {
		check(session.phase == Phase.PENDING)
		ExecutionTask(session.id).run()
	}

	@Suppress("CyclomaticComplexMethod") // The logic is just a big state machine
	private fun tickSession(session: SciconumScenarioSessionEntity): Instant? {
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

	private fun startPeerAssessmentPhase(session: SciconumScenarioSessionEntity): Instant? {
		// TODO: Assign answers
		return updateSession(session, Phase.PEER, Assessment.PEER_PHASE_DURATION)
	}

	private fun startPeerDebatePhase(session: SciconumScenarioSessionEntity): Instant? {
		// TODO: Assign peers
		return updateSession(session, Phase.PEER, Debate.PEER_PHASE_DURATION)
	}

	private fun updateSession(session: SciconumScenarioSessionEntity, phase: Phase, nextTickIn: Duration?): Instant? {
		val nextTick = nextTickIn?.let { clock.now().plus(nextTickIn) }
		LOGGER.trace("Transitioning session ${session.id} (${session.phase} -> $phase)")

		session.phase = phase
		session.nextPhaseAt = nextTick
		sciconumLearnerSessionRepository.transitionAllLearnerSessionsOfSessionTo(session, phase, nextTick)

		return nextTick
	}

	private inner class ExecutionTask(private val sessionId: Uuid) : Runnable {
		override fun run() {
			transactionTemplate.execute {
				val session = checkNotNull(sciconumScenarioSessionRepository.findByIdOrNull(sessionId))
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
