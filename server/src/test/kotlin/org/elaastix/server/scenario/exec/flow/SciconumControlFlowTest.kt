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

import org.elaastix.commons.platform.debt.SciconumTechDebt
import org.elaastix.server.scenario.SciconumScenario
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import kotlin.time.Duration.Companion.minutes
import org.elaastix.server.scenario.exec.SciconumScenarioExecutionPhase as Phase

@SpringBootTest
@OptIn(SciconumTechDebt::class)
class SciconumControlFlowTest : AbstractSciconumFlowTest() {
	@Test
	fun `control sequence executes according to planned scenario`() {
		validateScenario(SciconumScenario.CONTROL, 4u, 2u) {
			assertThatAllSessions().areInPhase(Phase.PENDING)

			start()
			assertThatAllSessions()
				.areInPhase(Phase.QUESTION)
				.areAtNthQuestion(1u)

			checkTransitionToPhaseAfter(phase = Phase.FEEDBACK, after = 2.minutes)
			assertThatAllSessions().areAtNthQuestion(1u)

			checkTransitionToPhaseAfter(phase = Phase.QUESTION, after = 0.5.minutes)
			assertThatAllSessions().areAtNthQuestion(2u)

			checkTransitionToPhaseAfter(phase = Phase.FEEDBACK, after = 2.minutes)
			assertThatAllSessions().areAtNthQuestion(2u)

			checkTransitionToPhaseAfter(phase = Phase.QUESTION, after = 0.5.minutes)
			assertThatAllSessions().areAtNthQuestion(3u)

			checkTransitionToPhaseAfter(phase = Phase.FEEDBACK, after = 2.minutes)
			assertThatAllSessions().areAtNthQuestion(3u)

			checkTransitionToPhaseAfter(phase = Phase.QUESTION, after = 0.5.minutes)
			assertThatAllSessions().areAtNthQuestion(4u)

			checkTransitionToPhaseAfter(phase = Phase.FEEDBACK, after = 2.minutes)
			assertThatAllSessions().areAtNthQuestion(4u)

			checkTransitionToPhaseAfter(phase = Phase.END, after = 0.5.minutes)
		}
	}

	@Test
	fun `control late learner can join and will be picked up on next question`() {
		validateScenario(SciconumScenario.CONTROL, 4u, 2u) {
			assertThatAllSessions().areInPhase(Phase.PENDING)
			start()

			checkTransitionToPhaseAfter(phase = Phase.QUESTION, after = 2.5.minutes)
			assertThatAllSessions().areAtNthQuestion(2u)

			val (_, lateLearnerSession) = createLearner()
			assertThatSpecificSession(lateLearnerSession).isInPhase(Phase.PENDING)

			checkTransitionToPhaseAfter(phase = Phase.FEEDBACK, after = 2.minutes)
			assertThatSpecificSession(lateLearnerSession).isInPhase(Phase.PENDING)
			assertThatAllSessions().areAtNthQuestion(2u)

			checkTransitionToPhaseAfter(phase = Phase.QUESTION, after = 0.5.minutes, lateLearnerSession)
			assertThatAllSessions(lateLearnerSession).areAtNthQuestion(3u)

			checkTransitionToPhaseAfter(phase = Phase.END, after = 5.minutes, lateLearnerSession)
		}
	}
}
