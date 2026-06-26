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

package org.elaastix.server.ws.events

import org.elaastix.server.users.entities.UserEntity
import org.springframework.context.ApplicationEvent
import org.springframework.web.socket.WebSocketSession

/**
 * Fired when a new client connects to the real-time WebSocket.
 *
 * @param source The object on which the event initially occurred or with which the event is associated.
 * @property user The user associated with the connection.
 * @property session The WebSocket session. Thread-safe.
 */
class WebSocketConnectEvent(source: Any, val user: UserEntity, val session: WebSocketSession) : ApplicationEvent(source)
