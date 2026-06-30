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

import kotlinx.serialization.Serializable
import org.elaastix.commons.data.Uuid
import org.elaastix.commons.orElseNotFound
import org.elaastix.commons.platform.debt.SciconumTechDebt
import org.elaastix.server.activities.response.dtos.ResponseSubmitDto
import org.elaastix.server.core.player.PlayerAction
import org.elaastix.server.core.player.PlayerController
import org.elaastix.server.core.player.PlayerQuery
import org.elaastix.server.scenario.exec.annotation.LearnerSession
import org.elaastix.server.scenario.exec.annotation.ScenarioSession
import org.elaastix.server.scenario.exec.entities.SciconumLearnerSessionEntity
import org.elaastix.server.scenario.exec.entities.SciconumScenarioSessionEntity
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * RPC endpoints for the response activity.
 */
@PlayerController
class ResponseActivityController(private val responseActivityService: ResponseActivityService) {
	/**
	 * Retrieve a question statement.
	 *
	 * The returned object does not include the expected answer and its explanation.
	 * This endpoint is suitable for retrieving a question to display to users.
	 *
	 * @param id The ID of the question.
	 * @return The question statement, and available choices for closed questions.
	 */
	@PlayerQuery("org.elaastix.response.getQuestion") // TODO: remove?
	fun getQuestion(@RequestParam id: Uuid) = responseActivityService.findQuestionStatement(id).orElseNotFound()

	/**
	 * Retrieve the current question statement.
	 *
	 * The returned object does not include the expected answer and its explanation.
	 *
	 * @param scenarioSession The scenario session.
	 * @param learnerSession The learner's session.
	 * @return The question statement, and available choices for closed questions.
	 */
	@PlayerQuery("org.elaastix.response.getCurrentQuestion")
	@OptIn(SciconumTechDebt::class)
	fun getCurrentQuestion(
		@ScenarioSession scenarioSession: SciconumScenarioSessionEntity,
		@LearnerSession learnerSession: SciconumLearnerSessionEntity,
	) = responseActivityService.findCurrentQuestionStatement(scenarioSession, learnerSession)

	/**
	 * Submit an answer to a question.
	 *
	 * @return The created response statement object.
	 * @throws org.elaastix.commons.exceptions.BadRequestException if the data is invalid.
	 */
	@ResponseStatus(HttpStatus.CREATED)
	@PlayerAction("org.elaastix.response.submitResponse") // TODO: remove?
	@OptIn(SciconumTechDebt::class)
	fun submitResponse(@RequestBody payload: SubmitAnswerDto) =
		responseActivityService.submitResponse(payload.questionId, payload.response)

	/**
	 * Submit an answer to the current question in the scenario question.
	 *
	 * @return The created response statement object.
	 * @throws org.elaastix.commons.exceptions.BadRequestException if the data is invalid.
	 */
	@ResponseStatus(HttpStatus.CREATED)
	@PlayerAction("org.elaastix.response.submitResponseToCurrentQuestion")
	@OptIn(SciconumTechDebt::class)
	fun submitResponse(
		@ScenarioSession scenarioSession: SciconumScenarioSessionEntity,
		@LearnerSession learnerSession: SciconumLearnerSessionEntity,
		@RequestBody payload: ResponseSubmitDto,
	) = responseActivityService.submitResponseToCurrentQuestion(scenarioSession, learnerSession, payload)

	/** Payload for the submitResponse action. */
	@Serializable
	data class SubmitAnswerDto(
		/** The question to send an answer for. */
		val questionId: Uuid,
		/** The response contents. */
		val response: ResponseSubmitDto,
	)
}
