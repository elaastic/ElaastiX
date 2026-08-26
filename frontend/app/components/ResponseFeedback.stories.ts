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

import ResponseFeedback from './ResponseFeedback.vue'
import {
	error,
	explanation,
	explanationMarkdown,
	learnerExplanation,
	learnerExplanationMarkdown,
	singleChoiceQuestion,
	questionMarkdown,
	lorem1000,
} from '~/lib/storiesProvider'

const meta = {
	title: 'ResponseFeedback',
	component: ResponseFeedback,
	tags: ['autodocs'],
} satisfies Meta<typeof ResponseFeedback>

export default meta
type Story = StoryObj<typeof meta>

export const Markdown: Story = {
	args: {
		isLoading: false,
		error: undefined,
		question: questionMarkdown,
		learnerChoice: 0,
		correctionChoice: 0,
		learnerExplanation: learnerExplanationMarkdown,
		explanatoryFeedback: explanationMarkdown,
	},
}

export const RightAnswer: Story = {
	args: {
		isLoading: false,
		error: undefined,
		question: singleChoiceQuestion,
		learnerChoice: 0,
		correctionChoice: 0,
		learnerExplanation: learnerExplanation,
		explanatoryFeedback: explanation,
	},
}

export const WrongAnswer: Story = {
	args: {
		isLoading: false,
		error: undefined,
		question: singleChoiceQuestion,
		learnerChoice: 2,
		correctionChoice: 0,
		learnerExplanation: learnerExplanation,
		explanatoryFeedback: explanation,
	},
}

export const LongContent: Story = {
	args: {
		isLoading: false,
		error: undefined,
		question: (() => {
			const questionCopy = structuredClone(singleChoiceQuestion)
			questionCopy.choices[3]!.content = lorem1000
			return questionCopy
		})(),
		learnerChoice: 2,
		correctionChoice: 0,
		learnerExplanation: (() => {
			const explanationCopy = structuredClone(learnerExplanation)
			explanationCopy.content = lorem1000
			return explanationCopy
		})(),
		explanatoryFeedback: explanation,
	},
}

export const Loading: Story = {
	args: {
		isLoading: true,
		error: undefined,
		question: undefined,
		learnerChoice: 2,
		correctionChoice: 0,
		learnerExplanation: learnerExplanation,
		explanatoryFeedback: explanation,
	},
}

export const Error: Story = {
	args: {
		isLoading: false,
		error: error,
		question: singleChoiceQuestion,
		learnerChoice: 2,
		correctionChoice: 0,
		learnerExplanation: learnerExplanation,
		explanatoryFeedback: explanation,
	},
}

export const WithoutAnswer: Story = {
	args: {
		isLoading: false,
		error: undefined,
		question: singleChoiceQuestion,
		learnerChoice: undefined,
		correctionChoice: 0,
		learnerExplanation: undefined,
		explanatoryFeedback: explanation,
	},
}
