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

import { decode } from 'cbor-x'
import {
	ScenarioTransitionMessage,
	type ScenarioTransitionMessageData,
} from '~/lib/ScenarioTransitionMessage'

export type WebSocketEvent = (this: WebSocket, event: Event) => void
export type WebSocketEventOnMessage = (
	data: ScenarioTransitionMessageData,
	event: Event,
) => void
export type WebSocketConfig = {
	onOpen: WebSocketEvent
	onMessage: WebSocketEventOnMessage
	onClose: WebSocketEvent
	onError: WebSocketEvent
}
export type WebSocketInteraction = {
	close: (code?: number | undefined, reason?: string | undefined) => void
}

export function useWebSocket(config: WebSocketConfig): WebSocketInteraction {
	const socket = new WebSocket(
		'ws://localhost:8080/player/org.elaastix.platform.rt',
	)
	socket.binaryType = 'arraybuffer'

	socket.onopen = config.onOpen
	socket.onmessage = (event) => {
		const decoded = decode(new Uint8Array(event.data))
		const typedValue = ScenarioTransitionMessage.create(decoded[1]).data
		config.onMessage(typedValue, event)
	}
	socket.onclose = config.onClose
	socket.onerror = config.onError

	const close = (code?: number | undefined, reason?: string | undefined) => {
		socket.close(code, reason)
	}

	return {
		close,
	}
}
