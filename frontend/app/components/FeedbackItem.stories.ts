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

const learnerExplanation = {
	content: 'Is it even a question',
	notBlank: true,
	notEmpty: true,
	$type: 'PlainText',
} satisfies components['schemas']['PlainText']

const explanation = {
	content: 'Because it is. signed the professor.',
	notBlank: true,
	notEmpty: true,
	$type: 'PlainText',
} satisfies components['schemas']['PlainText']

const learnerExplanationMarkdown = {
	content: '*Its obvious.*',
	notBlank: true,
	notEmpty: true,
	$type: 'MarkdownInline',
} satisfies components['schemas']['MarkdownText']

const explanationMarkdown = {
	content: 'See chapter 3 : `man kernel`.',
	notBlank: true,
	notEmpty: true,
	$type: 'MarkdownInline',
} satisfies components['schemas']['MarkdownText']

const questionMarkdown = {
	id: '0000000000000000000000000',
	$type: 'ClosedQuestion',
	statement: {
		$type: 'Markdown',
		content: `### Choix d'un système d'exploitation

Considérez les critères suivants :
- **Stabilité** du noyau (*kernel*)
- Support des environments de contenders (\`Docker\`, \`Podman\`)
- Flexibilité et gestion des droits (POSIX)

Quel est le **meilleur** système d'exploitation polyvalent ?`,
	},
	multiple: false,
	choices: [
		{ $type: 'MarkdownInline', content: '**Linux** (`GNU/Linux`)' },
		{ $type: 'MarkdownInline', content: '**Windows** (`NT Kernel`)' },
		{ $type: 'MarkdownInline', content: '**Darwin** (*macOS*)' },
		{ $type: 'MarkdownInline', content: '**OpenBSD** (*Focus sécurité*)' },
	],
} satisfies components['schemas']['ClosedQuestionStatementDto'] | undefined

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
		question: question,
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
		question: (() => {
			const questionCopy = structuredClone(question)
			questionCopy.choices[3]!.content += ' ' + 'a'.repeat(500)
			return questionCopy
		})(),
		learnerChoice: 2,
		correctionChoice: 0,
		learnerExplanation: (() => {
			const explanationCopy = structuredClone(learnerExplanation)
			explanationCopy.content += ' ' + 'a'.repeat(1000)
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
		error: {
			name: 'this is an error name',
			message: 'this is an error message',
		},
		question: question,
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
		question: question,
		learnerChoice: undefined,
		correctionChoice: 0,
		learnerExplanation: undefined,
		explanatoryFeedback: explanation,
	},
}
