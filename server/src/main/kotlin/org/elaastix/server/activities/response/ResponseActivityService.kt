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

package org.elaastix.server.activities.response

import jakarta.transaction.Transactional
import jakarta.validation.Valid
import org.elaastix.commons.data.Uuid
import org.elaastix.commons.inherits
import org.elaastix.commons.orNotFound
import org.elaastix.commons.platform.wip.UnclearAuthorshipOwnership
import org.elaastix.commons.validate
import org.elaastix.server.activities.response.dtos.ClosedQuestionStatementDto
import org.elaastix.server.activities.response.dtos.ClosedResponseDto
import org.elaastix.server.activities.response.dtos.ClosedResponseSubmitDto
import org.elaastix.server.activities.response.dtos.OpenQuestionStatementDto
import org.elaastix.server.activities.response.dtos.OpenResponseDto
import org.elaastix.server.activities.response.dtos.OpenResponseSubmitDto
import org.elaastix.server.activities.response.dtos.ResponseDto
import org.elaastix.server.activities.response.dtos.ResponseSubmitDto
import org.elaastix.server.activities.response.entities.ClosedQuestionEntity
import org.elaastix.server.activities.response.entities.ClosedResponseEntity
import org.elaastix.server.activities.response.entities.OpenQuestionEntity
import org.elaastix.server.activities.response.entities.OpenResponseEntity
import org.elaastix.server.activities.response.entities.ResponseEntity
import org.elaastix.server.activities.response.entities.projections.QuestionStatementProjection
import org.elaastix.server.activities.response.repositories.QuestionRepository
import org.elaastix.server.activities.response.repositories.ResponseRepository
import org.springframework.stereotype.Service

/** Service responsible for the response activity. */
@Service
class ResponseActivityService(
	private val questionRepository: QuestionRepository,
	private val responseRepository: ResponseRepository,
) {
	companion object {
		/** Transforms the DAO-level type into a Service-level type. */
		fun QuestionStatementProjection.toDto() =
			when {
				type inherits OpenQuestionEntity::class ->
					OpenQuestionStatementDto(id, statement)

				type inherits ClosedQuestionEntity::class ->
					@Suppress("UnsafeCallOnNullableType") // Needed to deal with projection shenanigans
					ClosedQuestionStatementDto(id, statement, multiple!!, choices!!)

				else -> error("Unknown polymorphic variant $type")
			}

		/** Transforms a [ResponseEntity] into a [ResponseDto]. */
		@OptIn(UnclearAuthorshipOwnership::class)
		fun ResponseEntity<*, *>.toDto() =
			when (this) {
				is OpenResponseEntity ->
					OpenResponseDto(
						id,
						author.id,
						question.id,
						amendedResponse?.id,
						answer,
						selfExplanation,
						confidenceDegree,
					)

				is ClosedResponseEntity ->
					ClosedResponseDto(
						id,
						author.id,
						question.id,
						amendedResponse?.id,
						answer,
						selfExplanation,
						confidenceDegree,
					)

				else -> error("Unknown polymorphic variant ${this::class}")
			}
	}

	/**
	 * Finds a question's statement (and available choices if it's a closed question).
	 */
	fun findQuestionStatement(id: Uuid) = questionRepository.findQuestionStatementById(id)?.toDto()

	/**
	 * Validates and records a response to a question.
	 */
	@Transactional
	fun submitResponse(questionId: Uuid, @Valid response: ResponseSubmitDto): ResponseDto {
		val statement = questionRepository.findQuestionStatementById(questionId).orNotFound()

		val entity =
			when (val questionRef = questionRepository.getTypedReferenceById(statement.type, statement.id)) {
				is OpenQuestionEntity -> {
					validate(response is OpenResponseSubmitDto) { "Response type does not match the question's type." }
					OpenResponseEntity(
						question = questionRef,
						answer = response.answer,
						selfExplanation = response.selfExplanation,
						confidenceDegree = response.confidenceDegree,
					)
				}

				is ClosedQuestionEntity -> {
					validate(response is ClosedResponseSubmitDto) { "Response type does not match the question's type." }
					validateClosedAnswer(response, statement)
					ClosedResponseEntity(
						question = questionRef,
						answer = response.answer,
						selfExplanation = response.selfExplanation,
						confidenceDegree = response.confidenceDegree,
					)
				}

				// COVERAGE: Unreachable
				else -> error("Unknown question type encountered: ${questionRef::class.java}")
			}

		val responseEntity = responseRepository.persist(entity)
		return responseEntity.toDto()
	}

	@Suppress("UnsafeCallOnNullableType") // SAFETY: We can safely assume [statement] is a closed question projection.
	private fun validateClosedAnswer(response: ClosedResponseSubmitDto, statement: QuestionStatementProjection) =
		when (response.answer) {
			is ClosedAnswer.Single -> {
				validate(!statement.multiple!!) { "Question does not accept single answers." }
				response.answer.value?.let {
					validate(it < statement.choices!!.size.toUInt()) { "Invalid answer." }
				}
			}

			is ClosedAnswer.Multiple -> {
				validate(statement.multiple!!) { "Question does not accept multiple answers." }
				validate(response.answer.value.all { it < statement.choices!!.size.toUInt() }) { "Invalid answer." }
			}
		}
}
