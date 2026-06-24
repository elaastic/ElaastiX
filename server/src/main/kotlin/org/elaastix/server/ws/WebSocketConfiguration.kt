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

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import org.elaastix.commons.ws.WebSocketEventPublisher
import org.elaastix.commons.ws.WebSocketMessageFormatter
import org.elaastix.commons.ws.WebSocketSessionHolder
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import kotlin.time.Clock

/**
 * Configuration class setting up the WebSocket server used for real-time events.
 */
@Configuration
@EnableWebSocket
class WebSocketConfiguration(
	private val clock: Clock,
	private val applicationEventPublisher: ApplicationEventPublisher,
) : WebSocketConfigurer {
	// TODO: consider making this more generic, but this may end up provided as a starter in future versions of Boot
	@OptIn(ExperimentalSerializationApi::class)
	private val cbor = Cbor {
		useDefiniteLengthEncoding = true
		encodeDefaults = false
	}

	override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
		registry.addHandler(wsHandler(), "/player/org.elaastix.platform.rt")
	}

	@Bean
	fun wsHandler() = WebSocketHandler(wsSessionHolder(), applicationEventPublisher)

	@Bean
	@OptIn(ExperimentalSerializationApi::class)
	fun wsMessageFormatter() = WebSocketMessageFormatter(cbor)

	@Bean
	fun wsSessionHolder() = WebSocketSessionHolder(clock)

	@Bean
	fun wsEventPublisher() = WebSocketEventPublisher(wsSessionHolder(), wsMessageFormatter())
}
