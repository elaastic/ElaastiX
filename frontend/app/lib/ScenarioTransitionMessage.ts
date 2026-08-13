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

export enum State {
	PENDING = 'PENDING',
	RUNNING = 'RUNNING',
	PAUSED = 'PAUSED',
	END = 'END',
}

export enum SciconumScenarioExecutionPhase {
	PENDING = 'PENDING',
	QUESTION = 'QUESTION',
	PEER = 'PEER',
	REVISE = 'REVISE',
	FEEDBACK = 'FEEDBACK',
	END = 'END',
}

export type ScenarioTransitionMessageData = v.InferOutput<
	typeof ScenarioTransitionMessage.schema
>

export const durationSchema = v.pipe(
	v.string(),
	v.rawTransform<string, Temporal.Duration>(
		({ dataset, addIssue, NEVER }) => {
			if (!dataset.typed) {
				return NEVER
			}

			try {
				return Temporal.Duration.from(dataset.value)
			} catch (error) {
				addIssue({
					received: dataset.value,
					message:
						error instanceof Error
							? error.message
							: 'Invalid duration format',
				})
				return NEVER
			}
		},
	),
)

export class ScenarioTransitionMessage {
	public data: ScenarioTransitionMessageData

	private constructor(data: ScenarioTransitionMessageData) {
		this.data = data
	}

	public static schema = v.object({
		sciconumPhase: v.pipe(v.enum(SciconumScenarioExecutionPhase)),
		state: v.pipe(v.enum(State)),
		duration: v.pipe(v.nullable(durationSchema)),
		currentRound: v.pipe(v.number()),
	})

	public static create(data: object) {
		const typedValue = v.parse(ScenarioTransitionMessage.schema, data)
		return new ScenarioTransitionMessage(typedValue)
	}
}
