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
import { multipleChoiceQuestionMarkdown, singleChoiceQuestion } from '~/lib/storiesProvider'

const meta = {
	title: 'Response Activity/Closed Question',
	component: ClosedQuestion,
	tags: ['autodocs'],
} satisfies Meta<typeof ClosedQuestion>

export default meta
type Story = StoryObj<typeof meta>

export const SingleChoice: Story = {
	args: {
		question: singleChoiceQuestion,
	},
}

export const MultipleChoice: Story = {
	args: {
		question: multipleChoiceQuestionMarkdown,
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
