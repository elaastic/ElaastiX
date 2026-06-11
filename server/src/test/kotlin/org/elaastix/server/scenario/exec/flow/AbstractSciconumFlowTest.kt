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

import org.elaastix.commons.jpa.entity.AbstractEntity
import org.elaastix.commons.platform.debt.SciconumTechDebt
import org.elaastix.server.assignments.AssignmentEntity
import org.elaastix.server.scenario.SciconumScenario
import org.elaastix.server.scenario.exec.ScenarioExecutionService
import org.elaastix.server.scenario.exec.SciconumIntegrationTest
import org.elaastix.server.scenario.exec.entities.SciconumSessionEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.time.Duration

@SpringBootTest
@OptIn(SciconumTechDebt::class)
abstract class AbstractSciconumFlowTest : SciconumIntegrationTest() {
	@Autowired
	lateinit var sciconumScenarioExecutionService: ScenarioExecutionService

	fun validateScenario(scenario: SciconumScenario, questionsCount: UInt, block: TimedFlowDsl.() -> Unit) {
		val assignment = createAssignment(1u, scenario, questionsCount)
		val session = sciconumSessionRepository.findAllByAssignment(assignment).single()

		TimedFlowDsl(assignment, session).apply(block)
	}

	inner class TimedFlowDsl(
		/**
		 * The assignment created for the test.
		 */
		val assignment: AssignmentEntity,

		/**
		 * The global session created for the test.
		 */
		val globalSession: SciconumSessionEntity,
	) {
		/**
		 * Creates a learner and adds them to the assignment.
		 *
		 * ```kt
		 * validateScenario(SciconumScenario.CONTROL, 9u) {
		 *     val (learner1, session1) = createLearner()
		 *     val (learner2, session2) = createLearner()
		 * }
		 * ```
		 */
		fun createLearner() =
			createUser()
				.also { assignmentParticipantsService.addParticipantToAssignment(assignment, it) }
				.let {
					val sess = sciconumLearnerSessionRepository.findOneByGlobalSessionAndLearner(globalSession, it)
					it to checkNotNull(sess)
				}

		/**
		 * Starts the execution of the test scenario.
		 */
		fun start() = sciconumScenarioExecutionService.startSequence(globalSession)

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

		private typealias E = AbstractEntity

		private fun <T : E> T.refresh0(): T = em.find(this::class.java, id)

		@JvmName("refresh1")
		fun <T : E> T.refresh(): T =
			tx.execute { refresh0() }

		fun <T : E> refresh(e1: T): T =
			tx.execute { e1.refresh0() }

		fun <T : E, U : E> refresh(e1: T, e2: U): Pair<T, U> =
			tx.execute { Pair(e1.refresh0(), e2.refresh0()) }

		fun <T : E, U : E, V : E> refresh(e1: T, e2: U, e3: V): Triple<T, U, V> =
			tx.execute { Triple(e1.refresh0(), e2.refresh0(), e3.refresh0()) }

		fun <T : E, U : E, V : E, W : E> refresh(e1: T, e2: U, e3: V, e4: W): Quadruple<T, U, V, W> =
			tx.execute { Quadruple(e1.refresh0(), e2.refresh0(), e3.refresh0(), e4.refresh0()) }
	}

	data class Quadruple<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)
}
