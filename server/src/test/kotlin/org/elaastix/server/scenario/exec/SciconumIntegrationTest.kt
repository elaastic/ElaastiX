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

import org.elaastix.commons.makeList
import org.elaastix.commons.platform.debt.SciconumTechDebt
import org.elaastix.commons.security.Role
import org.elaastix.mm.content.PlainText
import org.elaastix.server.activities.response.ClosedAnswer
import org.elaastix.server.activities.response.entities.ClosedQuestionEntity
import org.elaastix.server.activities.response.repositories.QuestionRepository
import org.elaastix.server.assignments.AssignmentEntity
import org.elaastix.server.assignments.AssignmentRepository
import org.elaastix.server.assignments.AssignmentService
import org.elaastix.server.assignments.dto.CreateAssignmentDto
import org.elaastix.server.assignments.participants.AssignmentParticipantsService
import org.elaastix.server.scenario.SciconumScenario
import org.elaastix.server.scenario.exec.repositories.SciconumLearnerSessionRepository
import org.elaastix.server.scenario.exec.repositories.SciconumScenarioSessionRepository
import org.elaastix.server.sequences.SciconumSequenceEntity
import org.elaastix.server.sequences.SequenceEntity
import org.elaastix.server.sequences.SequenceRepository
import org.elaastix.server.users.UserRepository
import org.elaastix.server.users.entities.UserEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import testutils.ControllableClock
import testutils.IntegrationTest
import testutils.WithMockUser
import testutils.email

@SpringBootTest
@WithMockUser(roles = [Role.WRITER])
@OptIn(SciconumTechDebt::class)
abstract class SciconumIntegrationTest : IntegrationTest() {
	@Autowired
	lateinit var clock: ControllableClock

	@Autowired
	lateinit var userRepository: UserRepository

	@Autowired
	lateinit var questionRepository: QuestionRepository

	@Autowired
	lateinit var sequenceRepository: SequenceRepository

	@Autowired
	lateinit var assignmentRepository: AssignmentRepository

	@Autowired
	lateinit var sciconumScenarioSessionRepository: SciconumScenarioSessionRepository

	@Autowired
	lateinit var sciconumLearnerSessionRepository: SciconumLearnerSessionRepository

	@Autowired
	lateinit var assignmentService: AssignmentService

	@Autowired
	lateinit var assignmentParticipantsService: AssignmentParticipantsService

	fun createAssignment(sequencesCount: UInt, scenario: SciconumScenario, questionsPerSequence: UInt): AssignmentEntity =
		assignmentRepository.findById(
			assignmentService.createAssignment(
				CreateAssignmentDto(
					displayName = makeRecognisableName(),
					sequenceIds = makeList(sequencesCount) {
						createSequence(scenario, questionsPerSequence).id
					},
				),
			).id,
		).orElseThrow()

	fun createSequence(scenario: SciconumScenario, questionsCount: UInt): SequenceEntity =
		sequenceRepository.persist(
			SciconumSequenceEntity(
				name = makeRecognisableName(),
				sciconumScenario = scenario,
				sciconumQuestions = makeList(questionsCount) {
					questionRepository.persist(
						ClosedQuestionEntity(
							statement = PlainText(FAKER.lorem().sentence(10)),
							choices = makeList(4) { PlainText(FAKER.lorem().sentence(3)) },
							expectedAnswer = ClosedAnswer.Single(1u),
							answerExplanation = null,
							multiple = false,
						),
					)
				},
			),
		)

	fun createUser(): UserEntity =
		userRepository.persist(
			UserEntity(
				firstName = FAKER.name().firstName(),
				lastName = FAKER.name().lastName(),
				email = FAKER.email(),
			),
		)
}
