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

package org.elaastix.commons.ws

import org.elaastix.commons.data.Uuid
import org.springframework.web.socket.WebSocketMessage

/**
 * Event publisher that dispatches events to connected WebSocket clients.
 *
 * @see WebSocketSessionHolder
 */
class WebSocketEventPublisher(
	private val sessionHolder: WebSocketSessionHolder,

	@PublishedApi
	internal val formatter: WebSocketMessageFormatter,
) {
	/**
	 * Publishes a payload to all connections registered on the given broadcast scope.
	 */
	inline fun <reified T : Any> publishPayload(scope: Uuid, event: T) = publishMessage(scope, formatter.format(event))

	/**
	 * Publishes a WebSocket message to all connections registered on the given broadcast scope.
	 */
	fun publishMessage(scope: Uuid, message: WebSocketMessage<*>) {
		sessionHolder.getSessionsInBroadcastScope(scope).forEach {
			it.sendMessage(message)
		}
	}
}
