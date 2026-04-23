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

import org.elaastix.commons.data.Uuid
import org.elaastix.server.activities.response.dtos.ClosedQuestionStatementDto
import org.elaastix.server.activities.response.dtos.OpenQuestionStatementDto
import org.elaastix.server.activities.response.entities.ClosedQuestionEntity
import org.elaastix.server.activities.response.entities.OpenQuestionEntity
import org.elaastix.server.activities.response.entities.projections.QuestionStatementProjection
import org.elaastix.server.activities.response.repositories.QuestionRepository
import org.springframework.stereotype.Service

/** Service responsible for the response activity. */
@Service
class ResponseActivityService(private val questionRepository: QuestionRepository) {
	companion object {
		/** Transforms the DAO-level type into a Service-level type. */
		fun QuestionStatementProjection.toDto() =
			when {
				OpenQuestionEntity::class.java.isAssignableFrom(type) ->
					OpenQuestionStatementDto(id, statement)

				ClosedQuestionEntity::class.java.isAssignableFrom(type) ->
					ClosedQuestionStatementDto(id, statement, multiple!!, choices!!)

				else -> error("Unknown polymorphic variant $type")
			}
	}

	/**
	 * Finds a question's statement (and available choices if it's a closed question).
	 */
	fun findQuestionStatement(id: Uuid) = questionRepository.findQuestionStatementById(id)?.toDto()
}
