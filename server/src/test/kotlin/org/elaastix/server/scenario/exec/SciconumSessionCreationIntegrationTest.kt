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

import org.assertj.core.api.Assertions.assertThat
import org.elaastix.commons.makeList
import org.elaastix.commons.platform.debt.SciconumTechDebt
import org.elaastix.server.assignments.dto.CreateAssignmentDto
import org.elaastix.server.scenario.SciconumScenario
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@OptIn(SciconumTechDebt::class)
class SciconumSessionCreationIntegrationTest : SciconumIntegrationTest() {
	@Test
	fun `creating an assignment creates a session for each sequence`() {
		val sequences = makeList(3) { createSequence(SciconumScenario.CONTROL, 1u) }

		val assignmentDto = assignmentService.createAssignment(
			CreateAssignmentDto(
				displayName = makeRecognisableName(),
				sequenceIds = sequences.map { it.id },
			),
		)

		// -- then

		val assignment = assignmentRepository.getReferenceById(assignmentDto.id)
		val sessions = sciconumScenarioSessionRepository.findAllByAssignment(assignment)
		assertThat(sessions).hasSize(3)
	}

	@Test
	fun `adding a learner to an assignment creates a learner session for each sequence`() {
		val assignment = createAssignment(3u, SciconumScenario.CONTROL, 1u)
		val user = createUser()

		assignmentParticipantsService.addParticipantToAssignmentById(assignment.id, user.id)

		// -- then

		val sessions = sciconumLearnerSessionRepository.findAllByAssignmentAndLearner(assignment, user)
		assertThat(sessions).hasSize(3)
	}

	@Test
	fun `adding a learner to an assignment does not create a session for ended sequences`() {
		val assignment = createAssignment(3u, SciconumScenario.CONTROL, 1u)
		val user = createUser()

		tx.execute {
			val session = sciconumScenarioSessionRepository.findAllByAssignment(assignment).first()
			session.phase = SciconumScenarioExecutionPhase.END
		}

		assignmentParticipantsService.addParticipantToAssignmentById(assignment.id, user.id)

		// -- then

		val sessions = sciconumLearnerSessionRepository.findAllByAssignmentAndLearner(assignment, user)
		assertThat(sessions).hasSize(2)
	}
}
