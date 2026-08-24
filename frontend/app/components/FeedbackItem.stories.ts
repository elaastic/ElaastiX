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

import type { Meta, StoryObj } from '@nuxtjs/storybook'

import FeedbackItem from './FeedbackItem.vue'
import type { components } from '#open-fetch-schemas/api'

const meta = {
	title: 'FeedbackItem',
	component: FeedbackItem,
	tags: ['autodocs'],
} satisfies Meta<typeof FeedbackItem>

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
} satisfies components['schemas']['ClosedQuestionStatementDto'] | undefined

export const RightAnswer: Story = {
	args: {
		isLoading: false,
		error: undefined,
		question: question,
		learnerChoice: 0,
		correctionChoice: 0,
		learnerExplanation: 'Is it even a question',
		explanatoryFeedback: 'Because it is. signed the professor.',
	},
}

export const WrongAnswer: Story = {
	args: {
		isLoading: false,
		error: undefined,
		question: (() => {
			const questionCopy = structuredClone(question)
			questionCopy.choices[3]!.content = 'a'.repeat(500)
			return questionCopy
		})(),
		learnerChoice: 2,
		correctionChoice: 0,
		learnerExplanation: 'Is it even a question ' + 'a'.repeat(1500),
		explanatoryFeedback: 'Because it is. signed the professor.',
	},
}

export const Loading: Story = {
	args: {
		isLoading: true,
		error: undefined,
		question: undefined,
		learnerChoice: 2,
		correctionChoice: 0,
		learnerExplanation: 'Is it even a question',
		explanatoryFeedback: 'Because it is. signed the professor.',
	},
}

export const Error: Story = {
	args: {
		isLoading: false,
		error: {
			name: 'this is an error name',
			message: 'this is an error message',
		},
		question: question,
		learnerChoice: 2,
		correctionChoice: 0,
		learnerExplanation: 'Is it even a question',
		explanatoryFeedback: 'Because it is. signed the professor.',
	},
}

export const WithoutAnswer: Story = {
	args: {
		isLoading: false,
		error: undefined,
		question: question,
		learnerChoice: undefined,
		correctionChoice: 0,
		learnerExplanation: undefined,
		explanatoryFeedback: 'Because it is. signed the professor.',
	},
}
