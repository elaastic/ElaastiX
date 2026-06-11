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

package org.elaastix.server.scenario.exec.flow

import org.assertj.core.api.Assertions.assertThat
import org.elaastix.commons.makeMapN
import org.elaastix.commons.platform.debt.SciconumTechDebt
import org.elaastix.server.scenario.SciconumScenario
import org.elaastix.server.scenario.exec.ScenarioExecutionService
import org.elaastix.server.scenario.exec.SciconumIntegrationTest
import org.elaastix.server.scenario.exec.SciconumScenarioExecutionPhase
import org.elaastix.server.scenario.exec.entities.SciconumLearnerSessionEntity
import org.elaastix.server.users.entities.UserEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds

@SpringBootTest
@OptIn(SciconumTechDebt::class)
abstract class AbstractSciconumFlowTest : SciconumIntegrationTest() {
	@Autowired
	lateinit var sciconumScenarioExecutionService: ScenarioExecutionService

	fun validateScenario(
		scenario: SciconumScenario,
		questionsCount: UInt,
		initialLearnersCount: UInt,
		block: TimedFlowDsl.() -> Unit,
	) {
		TimedFlowDsl(scenario, questionsCount, initialLearnersCount).apply(block)
	}

	inner class TimedFlowDsl(scenario: SciconumScenario, questionsCount: UInt, initialLearnersCount: UInt) {
		/** The assignment created for the test. */
		val assignment = createAssignment(1u, scenario, questionsCount)

		/** The global session created for the test. */
		val scenarioSession = sciconumScenarioSessionRepository.findAllByAssignment(assignment).single()

		/** Initial learners (and their session) created for the test. */
		val learners: Map<UserEntity, SciconumLearnerSessionEntity> = makeMapN(initialLearnersCount, ::createLearner)

		/**
		 * Creates a learner and adds them to the assignment.
		 *
		 * ```kt
		 * validateScenario(SciconumScenario.CONTROL, 9u) {
		 *     val (learner, learnerSession) = createLearner()
		 *     ...
		 * }
		 * ```
		 */
		fun createLearner() =
			createUser()
				.also { assignmentParticipantsService.addParticipantToAssignmentById(assignment.id, it.id) }
				.let {
					val sess = sciconumLearnerSessionRepository.findOneByScenarioSessionAndLearner(scenarioSession, it)
					it to checkNotNull(sess)
				}

		/**
		 * Starts the execution of the test scenario.
		 */
		fun start() = sciconumScenarioExecutionService.startSequence(scenarioSession)

		/**
		 * Advances the clock by [duration].
		 *
		 * ```kt
		 * validateScenario(SciconumScenario.CONTROL, 9u) {
		 *     val (learner1, session1) by creatingLearner
		 *
		 *     advanceClock(1.minutes)
		 *
		 *     session1 satisfies { id == ... }
		 * }
		 * ```
		 */
		fun advanceClock(duration: Duration) = clock.add(duration)

		/**
		 * Checks that the session will transition to [phase] after exactly the duration set in [after].
		 * Can optionally take any number of session to check. Internally, this function uses [assertThatAllSessions].
		 *
		 * This will assert that the state does not change after `duration - 1ns`, but does so after `duration`.
		 */
		fun checkTransitionToPhaseAfter(
			phase: SciconumScenarioExecutionPhase,
			after: Duration,
			vararg sessions: SciconumLearnerSessionEntity,
		) {
			advanceClock(after - 1.nanoseconds)
			assertThatAllSessions(*sessions).areNotInPhase(phase)

			advanceClock(1.nanoseconds)
			assertThatAllSessions(*sessions).areInPhase(phase)
		}

		/**
		 * Prepares an assertion object with the sessions passed in arguments.
		 */
		fun assertThatSpecificSessions(vararg sessions: SciconumLearnerSessionEntity) =
			SessionsAssert(sessions)

		/**
		 * Prepares an assertion object with the sessions passed in arguments.
		 */
		fun assertThatSpecificSession(session: SciconumLearnerSessionEntity) =
			SessionAssert(session)

		/**
		 * Prepares an assertion object with the global session, initial learner session, as well as sessions
		 * passed in arguments.
		 *
		 * Initially created sessions **must not** be passed to this function.
		 */
		fun assertThatAllSessions(vararg sessions: SciconumLearnerSessionEntity) =
			AllSessionsAssert(sessions)

		/** Assertions on session. */
		inner class SessionAssert(learnerSession: SciconumLearnerSessionEntity) {
			private val learnerSession = tx.execute { learnerSession.freshCopy() }

			/** Asserts that the session is in the specified [phase]. */
			fun isInPhase(phase: SciconumScenarioExecutionPhase) = also {
				assertThat(learnerSession.phase).isEqualTo(phase)
			}

			/** Asserts that the session is in the specified [phase]. */
			fun isNotInPhase(phase: SciconumScenarioExecutionPhase) = also {
				assertThat(learnerSession.phase).isNotEqualTo(phase)
			}
		}

		/** Assertions on sessions. */
		inner class SessionsAssert(learnerSessions: Array<out SciconumLearnerSessionEntity>) {
			private val learnerSessions = tx.execute { learnerSessions.freshCopies() }

			init {
				require(this.learnerSessions.isNotEmpty())
			}

			/** Asserts that all sessions are in the specified [phase]. */
			fun areInPhase(phase: SciconumScenarioExecutionPhase) = also {
				for (session in learnerSessions) {
					assertThat(session.phase).isEqualTo(phase)
				}
			}

			/** Asserts that all sessions are in the specified [phase]. */
			fun areNotInPhase(phase: SciconumScenarioExecutionPhase) = also {
				for (session in learnerSessions) {
					assertThat(session.phase).isNotEqualTo(phase)
				}
			}
		}

		/** Assertions on sessions. */
		inner class AllSessionsAssert(extraSessions: Array<out SciconumLearnerSessionEntity>) {
			private val scenarioSession = tx.execute { this@TimedFlowDsl.scenarioSession.freshCopy() }
			private val learnerSessions = tx.execute { (learners.values + extraSessions).freshCopies() }

			/** Asserts that all sessions are in the specified [phase]. */
			fun areInPhase(phase: SciconumScenarioExecutionPhase) = also {
				assertThat(scenarioSession.phase).isEqualTo(phase)
				for (session in learnerSessions) {
					assertThat(session.phase).isEqualTo(phase)
				}
			}

			/** Asserts that all sessions are in the specified [phase]. */
			fun areNotInPhase(phase: SciconumScenarioExecutionPhase) = also {
				assertThat(scenarioSession.phase).isNotEqualTo(phase)
				for (session in learnerSessions) {
					assertThat(session.phase).isNotEqualTo(phase)
				}
			}

			/** Asserts that the global session is at the [question]-th question. */
			fun areAtNthQuestion(question: UInt) = also {
				require(question != 0u) // When we say it's "nth", it's ACTUALLY nth and not actually 0-indexed :)
				assertThat(scenarioSession.currentRound).isEqualTo(question - 1u)
			}

			/** Asserts that the global session is at the [question]-th question. */
			fun areNotAtNthQuestion(question: UInt) = also {
				require(question != 0u) // When we say it's "nth", it's ACTUALLY nth and not actually 0-indexed :)
				assertThat(scenarioSession.currentRound).isNotEqualTo(question - 1u)
			}
		}
	}
}
