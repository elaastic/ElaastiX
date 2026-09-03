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
import AnyQuestion from './AnyQuestion.vue'
import type { Meta, StoryObj } from '@nuxtjs/storybook'

const meta = {
	title: 'Response Activity / Any Question',
	component: AnyQuestion,
	tags: ['autodocs'],
} satisfies Meta<typeof AnyQuestion>

export default meta
type Story = StoryObj<typeof meta>

const closedQuestion = {
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
} satisfies components['schemas']['ClosedQuestionStatementDto']

const openQuestion = {
	id: '0000000000000000000000001',
	$type: 'OpenQuestion',
	statement: {
		$type: 'Markdown',
		content: 'Quel est le sens de la vie?',
	},
} satisfies components['schemas']['OpenQuestionStatementDto']

export const ClosedQuestion: Story = {
	args: {
		question: closedQuestion,
	},
}

export const OpenQuestion: Story = {
	args: {
		question: openQuestion,
	},
}
