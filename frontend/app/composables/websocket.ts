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

import * as v from 'valibot'
import { decode } from 'cbor-x'

enum State {
	PENDING = 'PENDING',
	RUNNING = 'RUNNING',
	PAUSED = 'PAUSED',
	END = 'END',
}

enum SciconumScenarioExecutionPhase {
	PENDING = 'PENDING',
	QUESTION = 'QUESTION',
	PEER = 'PEER',
	REVISE = 'REVISE',
	FEEDBACK = 'FEEDBACK',
	END = 'END',
}

export const scenarioTransitionMessageSchema = v.object({
	sciconumPhase: v.pipe(v.enum(SciconumScenarioExecutionPhase)),
	state: v.pipe(v.enum(State)),
	duration: v.pipe(v.nullable(v.string())),
})

export type ScenarioTransitionMessage = v.InferOutput<typeof scenarioTransitionMessageSchema>

export type WebSocketEvent = (this: WebSocket, event: Event) => void
export type WebSocketEventOnMessage = (data: ScenarioTransitionMessage, event: Event) => void
export type WebSocketAction = {
	onOpen: WebSocketEvent
	onMessage: WebSocketEventOnMessage
	onClose: WebSocketEvent
	onError: WebSocketEvent
}
export type WebSocketInteraction = {
	close: (code?: number | undefined, reason?: string | undefined) => void
}

export function useWebSocket(actions: WebSocketAction): WebSocketInteraction {
	const socket = new WebSocket('ws://localhost:8080/player/org.elaastix.platform.rt')
	socket.binaryType = 'arraybuffer'

	socket.onopen = actions.onOpen
	socket.onmessage = (event: Event) => {
		const decoded = decode(new Uint8Array(event.data))
		const typedValue = v.parse(scenarioTransitionMessageSchema, decoded[1])
		actions.onMessage(typedValue, event)
	}
	socket.onclose = actions.onClose
	socket.onerror = actions.onError

	const close = () => {
		socket.close()
	}

	return {
		close,
	}
}
