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

import ClosedQuestion from './ClosedQuestion.vue'

const meta = {
	title: 'Response Activity/Closed Question',
	component: ClosedQuestion,
	tags: ['autodocs'],
} satisfies Meta<typeof ClosedQuestion>

export default meta
type Story = StoryObj<typeof meta>

export const SingleChoice: Story = {
	args: {
		question: {
			id: '0000000000000000000000000',
			$type: 'ClosedQuestion',
			statement: { $type: 'Markdown', content: 'What is the best multipurpose operating system?' },
			multiple: false,
			choices: [
				{ $type: 'PlainText', content: 'Linux' },
				{ $type: 'PlainText', content: 'Windows' },
				{ $type: 'PlainText', content: 'Darwin (macOS)' },
				{ $type: 'PlainText', content: 'OpenBSD' },
			],
		},
	},
}

export const MultipleChoice: Story = {
	args: {
		question: {
			id: '0000000000000000000000000',
			$type: 'ClosedQuestion',
			statement: { $type: 'Markdown', content: '**TRUE OR FALSE**. Software written in Haskell is guaranteed to have no side-effects.' },
			multiple: true,
			choices: [
				{ $type: 'MarkdownInline', content: '**TRUE**. Haskell is a functional programming language, and therefore pure' },
				{ $type: 'MarkdownInline', content: '**FALSE**. It is impossible to write side-effect-free code' },
				{ $type: 'MarkdownInline', content: '**TRUE**. No one run Haskell software, and therefore no side-effect ever occurs' },
				{ $type: 'MarkdownInline', content: '**FALSE**. Haskell wraps "impure" logic using monadic structures' },
			],
		},
	},
}

export const WithoutExplanation: Story = {
	args: {
		question: SingleChoice.args.question,
		requestSelfExplanation: false,
	},
}

export const AnswerOnly: Story = {
	args: {
		question: SingleChoice.args.question,
		requestSelfExplanation: false,
		confidenceDegreeOptions: false,
	},
}

export const Submitting: Story = {
	args: {
		question: SingleChoice.args.question,
		submitting: true,
	},
}

export const GeneralError: Story = {
	args: {
		question: SingleChoice.args.question,
		error: 'By the way, have I told you I use Arch Linux?',
	},
}
