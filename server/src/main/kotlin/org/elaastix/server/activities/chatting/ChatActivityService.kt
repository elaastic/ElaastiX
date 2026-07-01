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
import org.elaastix.commons.ws.WebSocketEventPublisher
import org.elaastix.mm.content.FormattedText
import org.elaastix.server.activities.chatting.dto.ChatterDto
import org.elaastix.server.activities.chatting.entities.ChatMessageEntity
import org.elaastix.server.activities.chatting.entities.ChatterEntity
import org.elaastix.server.activities.chatting.repositories.ChatMessageRepository
import org.elaastix.server.activities.chatting.repositories.ChatterRepository
import org.elaastix.server.activities.chatting.ws.ChatMessageMessage
import org.elaastix.server.scenario.SciconumScenario
import org.elaastix.server.scenario.exec.ScenarioExecutionStatusCheckService
import org.elaastix.server.scenario.exec.SciconumScenarioExecutionPhase
import org.elaastix.server.scenario.exec.entities.SciconumLearnerSessionEntity
import org.elaastix.server.scenario.exec.entities.SciconumScenarioSessionEntity
import org.elaastix.server.scenario.exec.repositories.SciconumChatPeeringRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service responsible for the chat activity.
 */
@Service
class ChatActivityService @SciconumTechDebt constructor(
	private val chatPeeringRepository: SciconumChatPeeringRepository,
	private val chatterRepository: ChatterRepository,
	private val chatMessageRepository: ChatMessageRepository,
	private val webSocketEventPublisher: WebSocketEventPublisher,
	private val statusCheckService: ScenarioExecutionStatusCheckService,
) {
	companion object {
		/** Maps a [ChatterEntity] to a [ChatterDto]. */
		fun ChatterEntity.toDto() = ChatterDto(id = id, nickname = nickname)

		/** Maps a [ChatMessageEntity] to a [ChatMessageMessage]. */
		fun ChatMessageEntity.toMessage() = ChatMessageMessage(chatter.toDto(), message)
	}

	/**
	 * Sends a message in the chat.
	 */
	@Transactional
	@OptIn(SciconumTechDebt::class)
	fun sendMessage(
		scenarioSession: SciconumScenarioSessionEntity,
		learnerSession: SciconumLearnerSessionEntity,
		message: FormattedText,
	) {
		statusCheckService.assertAppropriateScenarioState(
			scenarioSession,
			learnerSession,
			requiredScenario = SciconumScenario.PEER_DEBATE,
			requiredPhase = SciconumScenarioExecutionPhase.PEER,
		)

		val peering = chatPeeringRepository.findCurrentOneByLearnerSession(learnerSession)
			?: error("No peering for the current learner session?!")

		val identity = chatterRepository.findChatterIdentityOfUserInPeering(learnerSession.learner, peering)
			?: error("No chatter identity for the current learner session?!")

		val msg = chatMessageRepository.persist(ChatMessageEntity(chatter = identity, message = message))
		webSocketEventPublisher.publishPayload(peering.id, msg.toMessage())
	}
}
