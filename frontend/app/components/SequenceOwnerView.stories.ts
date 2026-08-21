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
import SequenceOwnerView from './SequenceOwnerView.vue'
import type { Meta, StoryObj } from '@nuxtjs/storybook'
import type { components } from '#open-fetch-schemas/api'

const meta = {
	title: 'Sequence Owner View',
	component: SequenceOwnerView,
	tags: ['autodocs'],
} satisfies Meta<typeof SequenceOwnerView>

export default meta

type Story = StoryObj<typeof meta>

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
		state: State.PENDING,
		question: question,
	},
}

export const Running: Story = {
	args: {
		state: State.RUNNING,
		question: question,
	},
}

export const Paused: Story = {
	args: {
		state: State.PAUSED,
		question: question,
	},
}

export const End: Story = {
	args: {
		state: State.END,
		question: question,
	},
}
