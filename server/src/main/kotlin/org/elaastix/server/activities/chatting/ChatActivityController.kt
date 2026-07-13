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

package org.elaastix.server.activities.chatting

import org.elaastix.commons.platform.debt.SciconumTechDebt
import org.elaastix.mm.content.FormattedText
import org.elaastix.server.core.player.PlayerAction
import org.elaastix.server.core.player.PlayerController
import org.elaastix.server.scenario.exec.annotation.LearnerSession
import org.elaastix.server.scenario.exec.annotation.ScenarioSession
import org.elaastix.server.scenario.exec.entities.SciconumLearnerSessionEntity
import org.elaastix.server.scenario.exec.entities.SciconumScenarioSessionEntity
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

@PlayerController
class ChatActivityController(private val chatActivityService: ChatActivityService) {
	/**
	 * Sends a message in the chat.
	 */
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PlayerAction("org.elaastix.chat.sendMessage")
	@OptIn(SciconumTechDebt::class)
	fun sendMessage(
		@ScenarioSession scenarioSession: SciconumScenarioSessionEntity,
		@LearnerSession learnerSession: SciconumLearnerSessionEntity,
		@RequestBody message: SendChatMessageDto,
	) {
		chatActivityService.sendMessage(scenarioSession, learnerSession, message.message)
	}

	/** Payload of a chat message. */
	data class SendChatMessageDto(
		/** The message contents. May be inline-formatted. */
		val message: FormattedText,
	)
}
