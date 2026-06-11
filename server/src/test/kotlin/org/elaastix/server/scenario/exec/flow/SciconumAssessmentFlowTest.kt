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
import org.elaastix.commons.platform.debt.SciconumTechDebt
import org.elaastix.server.scenario.SciconumScenario
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import kotlin.time.Duration.Companion.minutes
import org.elaastix.server.scenario.exec.SciconumScenarioExecutionPhase as Phase

@SpringBootTest
@OptIn(SciconumTechDebt::class)
class SciconumAssessmentFlowTest : AbstractSciconumFlowTest() {
	@Test
	fun `PA sequence executes according to planned scenario`() {
		validateScenario(SciconumScenario.PEER_ASSESSMENT, 4u) {
			val (_, session1) = createLearner()
			val (_, session2) = createLearner()

			fun checkState(phase: Phase, question: UInt) {
				val (gs, s1, s2) = refresh(scenarioSession, session1, session2)
				assertThat(gs.phase).isEqualTo(phase)
				assertThat(gs.currentRound).isEqualTo(question)
				assertThat(s1.phase).isEqualTo(phase)
				assertThat(s2.phase).isEqualTo(phase)
			}

			checkState(Phase.PENDING, 0u)
			start()
			checkState(Phase.QUESTION, 0u)

			advanceClock(2.minutes)
			checkState(Phase.PEER, 0u)
			// TODO: Check peering

			advanceClock(3.5.minutes)
			checkState(Phase.REVISE, 0u)

			advanceClock(0.5.minutes)
			checkState(Phase.FEEDBACK, 0u)

			advanceClock(1.5.minutes)
			checkState(Phase.QUESTION, 1u)

			advanceClock(2.minutes)
			checkState(Phase.PEER, 1u)
			// TODO: Check peering

			advanceClock(3.5.minutes)
			checkState(Phase.REVISE, 1u)

			advanceClock(0.5.minutes)
			checkState(Phase.FEEDBACK, 1u)

			advanceClock(1.5.minutes)
			checkState(Phase.QUESTION, 2u)

			advanceClock(2.minutes)
			checkState(Phase.PEER, 2u)
			// TODO: Check peering

			advanceClock(3.5.minutes)
			checkState(Phase.REVISE, 2u)

			advanceClock(0.5.minutes)
			checkState(Phase.FEEDBACK, 2u)

			advanceClock(1.5.minutes)
			checkState(Phase.QUESTION, 3u)

			advanceClock(2.minutes)
			checkState(Phase.PEER, 3u)
			// TODO: Check peering

			advanceClock(3.5.minutes)
			checkState(Phase.REVISE, 3u)

			advanceClock(0.5.minutes)
			checkState(Phase.FEEDBACK, 3u)

			advanceClock(1.5.minutes)
			checkState(Phase.END, 4u)
		}
	}

	@Test
	fun `PA late learner can join and will be picked up on next question`() {
		validateScenario(SciconumScenario.PEER_ASSESSMENT, 4u) {
			val (_, session1) = createLearner()
			val (_, session2) = createLearner()

			fun checkState(phase: Phase, question: UInt) {
				val (gs, s1, s2) = refresh(scenarioSession, session1, session2)
				assertThat(gs.phase).isEqualTo(phase)
				assertThat(gs.currentRound).isEqualTo(question)
				assertThat(s1.phase).isEqualTo(phase)
				assertThat(s2.phase).isEqualTo(phase)
			}

			checkState(Phase.PENDING, 0u)
			start()

			advanceClock(
				2.minutes +
					3.5.minutes +
					0.5.minutes +
					1.5.minutes,
			)
			checkState(Phase.QUESTION, 1u)

			val (_, session3) = createLearner()
			assertThat(session3.refresh().phase).isEqualTo(Phase.PENDING)

			advanceClock(2.minutes)
			checkState(Phase.PEER, 1u)
			assertThat(session3.refresh().phase).isEqualTo(Phase.PENDING)
			// TODO: Check learner has no peering

			advanceClock(3.5.minutes)
			checkState(Phase.REVISE, 1u)
			assertThat(session3.refresh().phase).isEqualTo(Phase.PENDING)

			advanceClock(0.5.minutes)
			checkState(Phase.FEEDBACK, 1u)
			assertThat(session3.refresh().phase).isEqualTo(Phase.PENDING)

			advanceClock(1.5.minutes)
			checkState(Phase.QUESTION, 2u)
			assertThat(session3.refresh().phase).isEqualTo(Phase.QUESTION)
		}
	}
}
