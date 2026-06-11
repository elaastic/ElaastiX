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
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import org.elaastix.server.scenario.exec.SciconumScenarioExecutionPhase as Phase

@SpringBootTest
@OptIn(SciconumTechDebt::class)
class SciconumScenarioControlTest : AbstractSciconumFlowTest() {
	@Test
	fun `pausing a sequence suspends its execution and can resume later`() {
		validateScenario(SciconumScenario.PEER_ASSESSMENT, 4u, 2u) {
			assertThatAllSessions().areInPhase(Phase.PENDING)
			start()

			assertThatAllSessions()
				.areInPhase(Phase.QUESTION)
				.areAtNthQuestion(1u)

			checkTransitionToPhaseAfter(phase = Phase.PEER, after = 2.minutes)
			assertThatAllSessions().areAtNthQuestion(1u)

			checkTransitionToPhaseAfter(phase = Phase.REVISE, after = 3.5.minutes)
			assertThatAllSessions().areAtNthQuestion(1u)

			checkTransitionToPhaseAfter(phase = Phase.FEEDBACK, after = 0.5.minutes)
			assertThatAllSessions().areAtNthQuestion(1u)

			advanceClock(1.minutes)
			assertThatAllSessions()
				.areInPhase(Phase.FEEDBACK)
				.areAtNthQuestion(1u)

			sciconumScenarioExecutionService.pauseSequenceScenarioSessionById(scenarioSession.id)

			advanceClock(1.hours)
			assertThatAllSessions()
				.areInPhase(Phase.FEEDBACK)
				.areAtNthQuestion(1u)

			sciconumScenarioExecutionService.resumeSequenceScenarioSessionById(scenarioSession.id)

			checkTransitionToPhaseAfter(phase = Phase.QUESTION, after = 0.5.minutes)
			assertThatAllSessions().areAtNthQuestion(2u)

			advanceClock(1.minutes)
			sciconumScenarioExecutionService.pauseSequenceScenarioSessionById(scenarioSession.id)

			// TODO: check submission not allowed

			sciconumScenarioExecutionService.resumeSequenceScenarioSessionById(scenarioSession.id)

			checkTransitionToPhaseAfter(phase = Phase.PEER, after = 1.minutes)
			assertThatAllSessions().areAtNthQuestion(2u)

			advanceClock(2.minutes)
			sciconumScenarioExecutionService.pauseSequenceScenarioSessionById(scenarioSession.id)

			// TODO: check submission not allowed

			sciconumScenarioExecutionService.resumeSequenceScenarioSessionById(scenarioSession.id)

			checkTransitionToPhaseAfter(phase = Phase.REVISE, after = 1.5.minutes)
			assertThatAllSessions().areAtNthQuestion(2u)
		}
	}
}
