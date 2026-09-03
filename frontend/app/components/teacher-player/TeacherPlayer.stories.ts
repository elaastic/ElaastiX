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

import { State } from '~/lib/ScenarioTransitionMessage'
import TeacherPlayer from './TeacherPlayer.vue'
import type { Meta, StoryObj } from '@nuxtjs/storybook'
import type { components } from '#open-fetch-schemas/api'
import type { SequenceExecution, SequenceTimebox } from '~/types/sequence'

const meta = {
	title: 'Player / Teacher Player',
	component: TeacherPlayer,
	tags: ['autodocs'],
} satisfies Meta<typeof TeacherPlayer>

export default meta

type Story = StoryObj<typeof meta>

const sequenceName = 'The Story sequence'

const timebox = {
	timeTotal: 60,
	timeElapsed: 30,
	isRunningOutOfTime: false,
} satisfies SequenceTimebox

const question = {
	id: '0000000000000000000000000',
	$type: 'ClosedQuestion',
	statement: {
		$type: 'Markdown',
		content: 'What is the best multipurpose operating system?',
	},
	multiple: false,
	choices: [
		{ $type: 'PlainText', content: 'Linux' },
		{ $type: 'PlainText', content: 'Windows' },
		{ $type: 'PlainText', content: 'Darwin (macOS)' },
		{ $type: 'PlainText', content: 'OpenBSD' },
	],
} satisfies
| components['schemas']['ClosedQuestionStatementDto']
| components['schemas']['OpenQuestionStatementDto']
| undefined

export const Pending: Story = {
	args: {
		sequenceExecution: {
			name: sequenceName,
			state: State.PENDING,
			phase: 'QUESTION',
			timebox,
			question,
		} satisfies SequenceExecution,
	},
}

export const Running: Story = {
	args: {
		sequenceExecution: {
			name: sequenceName,
			state: State.RUNNING,
			phase: 'QUESTION',
			timebox,
			question,
		} satisfies SequenceExecution,
	},
}

export const Paused: Story = {
	args: {
		sequenceExecution: {
			name: sequenceName,
			state: State.PAUSED,
			phase: 'QUESTION',
			timebox,
			question,
		} satisfies SequenceExecution,
	},
}

export const End: Story = {
	args: {
		sequenceExecution: {
			name: sequenceName,
			state: State.END,
			phase: 'QUESTION',
			timebox,
			question,
		} satisfies SequenceExecution,
	},
}
