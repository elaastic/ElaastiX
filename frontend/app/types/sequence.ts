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
import type { components } from '#open-fetch-schemas/api'
import type { State } from '~/lib/ScenarioTransitionMessage'

export interface SequenceExecution {
	/** The name of the sequence. */
	name: string

	/** The sequence state. */
	state: State | undefined

	/** A string representing the current phase of the sequence. */
	phase: string | undefined

	/** The timebox execution state when the sequence is timeboxed */
	timebox?: SequenceTimebox

	/** The question currently displayed by the sequence. */
	question:
		| components['schemas']['ClosedQuestionStatementDto']
		| components['schemas']['OpenQuestionStatementDto']
		| undefined
}

export interface SequenceTimebox {
	/** The total time in seconds allocated to the sequence. */
	timeTotal: number

	/** Number of seconds elapsed for the sequence. */
	timeElapsed: number

	/** Whether the sequence is considered running out of time. */
	isRunningOutOfTime: boolean
}
