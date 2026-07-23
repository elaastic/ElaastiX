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
import Sequence from './Sequence.vue'
import { FetchError } from 'ofetch'

const meta = {
	title: 'Sequence',
	component: Sequence,
	tags: ['autodocs'],
	decorators: [
		(story, context) => {
			const nuxtApp = useNuxtApp()

			nuxtApp.$api = async (url, options) => {
				console.log(
					`Mocked $api intercepted request for: ${url}`,
					options,
				)

				const mockConfig = context.parameters?.apiMock
				if (mockConfig) {
					if (mockConfig.shouldThrow) {
						throw mockConfig.error
					}
					return mockConfig.data
				}

				return {}
			}

			return story()
		},
	],
} satisfies Meta<typeof Sequence>

export default meta
type Story = StoryObj<typeof meta>

export const Success: Story = {
	render: args => ({
		components: { Sequence },
		setup() {
			return { args }
		},
		template: '<Sequence v-bind="args" />',
	}),
	args: {
		uuid: 'success-scenario-123',
	},
	parameters: {
		apiMock: {
			data: {
				uuid: 'success-scenario-123',
				phase: 'QUESTION',
				currentRound: 0,
				sequence: {
					name: 'Introduction to Storybook',
					sciconumQuestions: [
						{
							statement: {
								content:
									'How do you configure story-specific API mocks?',
							},
						},
					],
				},
			},
		},
	},
}

export const ServerError: Story = {
	render: args => ({
		components: { Sequence },
		setup() {
			return { args }
		},
		template: '<Sequence v-bind="args" />',
	}),
	args: {
		uuid: 'broken-scenario-456',
	},
	parameters: {
		apiMock: {
			shouldThrow: true,
			error: Object.assign(
				new FetchError('Internal Server Error (HTTP 500)'),
				{
					statusCode: 500,
					statusMessage: 'Internal Server Error',
				},
			),
		},
	},
}
