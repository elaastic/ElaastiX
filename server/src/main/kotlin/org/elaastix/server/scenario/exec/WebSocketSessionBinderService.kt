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
import org.elaastix.server.scenario.exec.entities.SciconumScenarioSessionEntity
import org.elaastix.server.scenario.exec.repositories.SciconumChatPeeringRepository
import org.elaastix.server.scenario.exec.repositories.SciconumLearnerSessionRepository
import org.elaastix.server.scenario.exec.repositories.SciconumScenarioSessionRepository
import org.elaastix.server.ws.events.WebSocketConnectEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.socket.WebSocketSession

/**
 * Service responsible for binding clients connecting to the real-time WebSocket to the appropriate broadcast scopes.
 */
@Service
@SciconumTechDebt
class WebSocketSessionBinderService(
	private val learnerSessionRepository: SciconumLearnerSessionRepository,
	private val scenarioSessionRepository: SciconumScenarioSessionRepository,
	private val chatPeeringRepository: SciconumChatPeeringRepository,
	private val webSocketSessionHolder: WebSocketSessionHolder,
) {
	/**
	 * Handles [WebSocketConnectEvent] by assigning it to the relevant broadcast scopes.
	 */
	@EventListener
	@Transactional
	fun handleUserJoin(event: WebSocketConnectEvent) {
		val learnerSessions = learnerSessionRepository.findAllByLearnerAndNotEnded(event.user)
		for (session in learnerSessions) {
			event.session.bindToSession(session)
		}

		val managedScenarioSessions = scenarioSessionRepository.findAllBySequenceOwnerAndPhaseNotEnded(event.user)
		for (session in managedScenarioSessions) {
			webSocketSessionHolder.assignToBroadcastScope(event.session, session.id)
		}
	}

	/**
	 * Assigns the currently open WebSocket sessions to the appropriate broadcast scopes.
	 */
	@Transactional(propagation = Propagation.MANDATORY)
	fun bindBroadcastScopesForSciconumSequenceSession(session: SciconumScenarioSessionEntity) {
		val sessions = learnerSessionRepository.findAllByScenarioSession(session)
		for (session in sessions) {
			for (ws in webSocketSessionHolder.getSessionsOfUser(session.learner.id)) {
				ws.bindToSession(session)
			}
		}
	}

	/**
	 * Frees the broadcast scopes of the given session.
	 * Processed asynchronously to not block the execution of the main thread.
	 */
	@Async
	@Transactional
	fun freeBroadcastScopesOfSession(session: SciconumScenarioSessionEntity) {
		val sessions = learnerSessionRepository.findAllByScenarioSession(session)
		val chatPeeringList = chatPeeringRepository.findAllByScenarioSession(session)

		for (session in sessions) webSocketSessionHolder.disbandBroadcastScope(session.id)
		for (peering in chatPeeringList) webSocketSessionHolder.disbandBroadcastScope(peering.id)
	}

	private fun WebSocketSession.bindToSession(session: SciconumLearnerSessionEntity) {
		webSocketSessionHolder.assignToBroadcastScope(this, session.id)

		if (session.isPeerDebatePhase()) {
			chatPeeringRepository.findCurrentOneByLearnerSession(session)?.let { peering ->
				webSocketSessionHolder.assignToBroadcastScope(this, peering.id)
			}
		}
	}

	private fun SciconumLearnerSessionEntity.isPeerDebatePhase() =
		phase == SciconumScenarioExecutionPhase.PEER &&
			scenarioSession.sequence.sciconumScenario == SciconumScenario.PEER_DEBATE
}
