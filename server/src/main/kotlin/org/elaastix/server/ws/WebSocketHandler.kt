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

package org.elaastix.server.ws

import org.apache.commons.logging.LogFactory
import org.elaastix.commons.ws.WebSocketSessionHolder
import org.elaastix.server.authn.ElaastixAuthentication
import org.elaastix.server.users.entities.UserEntity
import org.elaastix.server.ws.events.WebSocketConnectEvent
import org.elaastix.server.ws.events.WebSocketDisconnectEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.BinaryWebSocketHandler
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

/**
 * WebSocket connection handler managing client connections.
 * Since clients aren't expected to send anything, this just registers and unregisters sessions.
 */
class WebSocketHandler(
	private val sessionHolder: WebSocketSessionHolder,
	private val applicationEventPublisher: ApplicationEventPublisher,
) : BinaryWebSocketHandler() {
	companion object {
		private val LOGGER = LogFactory.getLog(WebSocketHandler::class.java)

		private const val SESSION_BUFFER_LIMIT = 10_000_000 // 10 MB
		private val SESSION_STALL_LIMIT = 1.minutes.toInt(DurationUnit.MILLISECONDS)
	}

	val WebSocketSession.user: UserEntity
		get() = (principal as? ElaastixAuthentication)?.principal ?: throw IllegalStateException(
			"Missing or invalid authentication",
		)

	override fun afterConnectionEstablished(session: WebSocketSession) {
		val user = session.user
		LOGGER.debug("Connection ${session.id} opened (User: ${user.id})")
		val safeSession = ConcurrentWebSocketSessionDecorator(session, SESSION_STALL_LIMIT, SESSION_BUFFER_LIMIT)
		sessionHolder.registerSession(session, user.id)
		applicationEventPublisher.publishEvent(WebSocketConnectEvent(this, user, safeSession))
	}

	override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
		LOGGER.debug("Connection ${session.id} closed: $status")
		sessionHolder.unregisterSession(session)
		applicationEventPublisher.publishEvent(WebSocketDisconnectEvent(this, session, status))
	}

	override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
		LOGGER.error("Transport error on connection ${session.id}", exception)
	}
}
