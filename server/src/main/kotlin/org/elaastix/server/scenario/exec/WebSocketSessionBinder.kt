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

import org.elaastix.commons.platform.debt.SciconumTechDebt
import org.elaastix.commons.ws.WebSocketSessionHolder
import org.elaastix.server.scenario.SciconumScenario
import org.elaastix.server.scenario.exec.entities.SciconumLearnerSessionEntity
import org.elaastix.server.scenario.exec.repositories.SciconumChatPeeringRepository
import org.elaastix.server.scenario.exec.repositories.SciconumLearnerSessionRepository
import org.elaastix.server.ws.events.WebSocketConnectEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@SciconumTechDebt
class WebSocketSessionBinder(
	private val learnerSessionRepository: SciconumLearnerSessionRepository,
	private val chatPeeringRepository: SciconumChatPeeringRepository,
	private val webSocketSessionHolder: WebSocketSessionHolder,
) {
	@EventListener
	@Transactional
	fun handleLearnerJoin(event: WebSocketConnectEvent) {
		val sessions = learnerSessionRepository.findAllByLearner(event.user)
		for (session in sessions) {
			webSocketSessionHolder.assignToBroadcastScope(event.session, session.id)

			if (session.isPeerDebatePhase()) {
				chatPeeringRepository.findOneByLearnerSession(session)?.let { peering ->
					webSocketSessionHolder.assignToBroadcastScope(event.session, peering.id)
				}
			}
		}
	}

	private fun SciconumLearnerSessionEntity.isPeerDebatePhase() =
		phase == SciconumScenarioExecutionPhase.PEER &&
			scenarioSession.sequence.sciconumScenario == SciconumScenario.PEER_DEBATE
}
