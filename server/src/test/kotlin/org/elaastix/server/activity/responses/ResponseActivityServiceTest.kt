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

package org.elaastix.server.activity.responses

import io.mockk.bdd.given
import io.mockk.bdd.then
import io.mockk.called
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.elaastix.commons.data.Uuid
import org.elaastix.commons.exceptions.BadRequestException
import org.elaastix.server.AuthenticatedUnitTest
import org.elaastix.server.activities.response.ClosedAnswer
import org.elaastix.server.activities.response.ResponseActivityService
import org.elaastix.server.activities.response.dtos.ClosedResponseDto
import org.elaastix.server.activities.response.dtos.ClosedResponseSubmitDto
import org.elaastix.server.activities.response.dtos.OpenResponseDto
import org.elaastix.server.activities.response.dtos.OpenResponseSubmitDto
import org.elaastix.server.activities.response.entities.ClosedQuestionEntity
import org.elaastix.server.activities.response.entities.OpenQuestionEntity
import org.elaastix.server.activities.response.entities.QuestionEntity
import org.elaastix.server.activities.response.entities.projections.QuestionStatementProjection
import org.elaastix.server.activities.response.repositories.QuestionRepository
import org.elaastix.server.activities.response.repositories.ResponseRepository
import org.elaastix.server.core.content.PlainText
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class ResponseActivityServiceTest : AuthenticatedUnitTest() {
	val questionRepo: QuestionRepository = mockk()
	val responseRepo: ResponseRepository = mockk()

	val service = ResponseActivityService(questionRepo, responseRepo)

	private inline fun <reified T : QuestionEntity> mockkStatementProjection(questionEntity: T) =
		mockk<QuestionStatementProjection> {
			given { type } returns T::class.java
			given { id } answers { questionEntity.id }
			given { statement } answers { questionEntity.statement }
			when (T::class) {
				OpenQuestionEntity::class -> {
					given { multiple } returns null
					given { choices } returns null
				}

				ClosedQuestionEntity::class -> {
					questionEntity as ClosedQuestionEntity
					given { multiple } answers { questionEntity.multiple }
					given { choices } answers { questionEntity.choices }
				}
			}
		}

	@Test
	fun `persists open response to an open question`() {
		val user = mockAuthenticatedUser()

		val questionId = Uuid.random()
		val questionEntity: OpenQuestionEntity = mockk {
			given { id } returns questionId
		}

		given { questionRepo.getEntityReferenceWithType<QuestionEntity>(questionId, any()) } returns questionEntity
		given { questionRepo.findQuestionStatementById(questionId) } returns mockkStatementProjection(questionEntity)
		given { responseRepo.persist(any()) } returnsArgument 0

		val response = assertDoesNotThrow {
			service.submitResponse(
				questionId,
				OpenResponseSubmitDto(
					answer = PlainText("meow"),
					selfExplanation = null,
					confidenceDegree = null,
				),
			)
		}

		assertThat(response).isInstanceOf(OpenResponseDto::class.java)
		if (response !is OpenResponseDto) error("Impossible") // Required for type cast

		assertThat(response.questionId).isEqualTo(questionId)
		assertThat(response.authorId).isEqualTo(user.id)

		then(exactly = 1) {
			responseRepo.persist(any())
		}
	}

	@Test
	fun `persists closed response (multiple) to a closed question (multiple)`() {
		val user = mockAuthenticatedUser()

		val questionId = Uuid.random()
		val questionEntity: ClosedQuestionEntity = mockk {
			given { id } returns questionId
			given { multiple } returns true
			given { choices } returns listOf(PlainText("1"), PlainText("2"))
		}

		given { questionRepo.getEntityReferenceWithType<QuestionEntity>(questionId, any()) } returns questionEntity
		given { questionRepo.findQuestionStatementById(questionId) } returns mockkStatementProjection(questionEntity)
		given { responseRepo.persist(any()) } returnsArgument 0

		val response = assertDoesNotThrow {
			service.submitResponse(
				questionId,
				ClosedResponseSubmitDto(
					answer = ClosedAnswer.Multiple(setOf(1u)),
					selfExplanation = null,
					confidenceDegree = null,
				),
			)
		}

		assertThat(response).isInstanceOf(ClosedResponseDto::class.java)
		if (response !is ClosedResponseDto) error("Impossible") // Required for type cast

		assertThat(response.questionId).isEqualTo(questionId)
		assertThat(response.authorId).isEqualTo(user.id)
		assertThat(response.answer).isInstanceOf(ClosedAnswer.Multiple::class.java)

		then(exactly = 1) {
			responseRepo.persist(any())
		}
	}

	@Test
	fun `rejects mismatched question and response types`() {
		mockAuthenticatedUser()

		val questionId = Uuid.random()
		val questionEntity: ClosedQuestionEntity = mockk {
			given { id } returns questionId
		}

		given { questionRepo.getEntityReferenceWithType<QuestionEntity>(questionId, any()) } returns questionEntity
		given { questionRepo.findQuestionStatementById(questionId) } returns mockkStatementProjection(questionEntity)
		given { responseRepo.persist(any()) } returnsArgument 0

		assertThrows<BadRequestException> {
			service.submitResponse(
				questionId,
				OpenResponseSubmitDto(
					answer = PlainText("meow"),
					selfExplanation = null,
					confidenceDegree = null,
				),
			)
		}

		then {
			responseRepo wasNot called
		}
	}

	@Test
	fun `rejects bad closed response type for a question`() {
		mockAuthenticatedUser()

		val questionId = Uuid.random()
		val questionEntity: ClosedQuestionEntity = mockk {
			given { id } returns questionId
			given { multiple } returns true
			given { choices } returns listOf(PlainText("1"), PlainText("2"))
		}

		given { questionRepo.getEntityReferenceWithType<QuestionEntity>(questionId, any()) } returns questionEntity
		given { questionRepo.findQuestionStatementById(questionId) } returns mockkStatementProjection(questionEntity)
		given { responseRepo.persist(any()) } returnsArgument 0

		assertThrows<BadRequestException> {
			service.submitResponse(
				questionId,
				ClosedResponseSubmitDto(
					answer = ClosedAnswer.Single(1u),
					selfExplanation = null,
					confidenceDegree = null,
				),
			)
		}

		then {
			responseRepo wasNot called
		}
	}

	@Test
	fun `rejects closed responses with out of bounds answers`() {
		mockAuthenticatedUser()

		val questionId = Uuid.random()
		val questionEntity: ClosedQuestionEntity = mockk {
			given { id } returns questionId
			given { multiple } returns true
			given { choices } returns listOf(PlainText("1"), PlainText("2"))
		}

		given { questionRepo.getEntityReferenceWithType<QuestionEntity>(questionId, any()) } returns questionEntity
		given { questionRepo.findQuestionStatementById(questionId) } returns mockkStatementProjection(questionEntity)
		given { responseRepo.persist(any()) } returnsArgument 0

		assertThrows<BadRequestException> {
			service.submitResponse(
				questionId,
				ClosedResponseSubmitDto(
					answer = ClosedAnswer.Single(1337u),
					selfExplanation = null,
					confidenceDegree = null,
				),
			)
		}

		then {
			responseRepo wasNot called
		}
	}
}
