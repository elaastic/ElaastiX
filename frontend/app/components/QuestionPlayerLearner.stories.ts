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
import QuestionPlayerLearner from './QuestionPlayerLearner.vue'
import type { Meta, StoryObj } from '@nuxtjs/storybook'

const meta = {
	title: 'Learner player / Question phase',
	component: QuestionPlayerLearner,
	tags: ['autodocs'],
} satisfies Meta<typeof QuestionPlayerLearner>

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

export const Question: Story = {
	args: {
		question: question,
	},
}
