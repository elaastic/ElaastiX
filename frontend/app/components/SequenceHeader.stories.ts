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

import SequenceHeader from './SequenceHeader.vue'
import type { Meta, StoryObj } from '@nuxtjs/storybook'
import {
	SciconumScenarioExecutionPhase,
	State,
} from '~/lib/ScenarioTransitionMessage'
import type { SequenceExecution } from '~/types/sequence'

type StoryArgs = {
	name: SequenceExecution['name']
	state: SequenceExecution['state']
	phase: SequenceExecution['phase']
}
& ({
	timeboxed: false
} | {
	timeboxed: true
	timeTotal: number
	timeElapsed: number
	isRunningOutOfTime: boolean
})

const meta = {
	title: 'Sequence Header',
	component: SequenceHeader,
	tags: ['autodocs'],
	argTypes: {
		state: {
			control: 'select',
			options: [State.PENDING, State.RUNNING, State.PAUSED, State.END],
		},
		phase: {
			control: 'select',
			options: [
				SciconumScenarioExecutionPhase.PENDING,
				SciconumScenarioExecutionPhase.QUESTION,
				SciconumScenarioExecutionPhase.PEER,
				SciconumScenarioExecutionPhase.FEEDBACK,
				SciconumScenarioExecutionPhase.REVISE,
				SciconumScenarioExecutionPhase.END,
			],
		},
		timeTotal: {
			control: 'number',
			if: {
				arg: 'timeboxed',
				truthy: true,
			},
		},

		timeElapsed: {
			control: 'number',
			if: {
				arg: 'timeboxed',
				truthy: true,
			},
		},

		isRunningOutOfTime: {
			control: 'boolean',
			if: {
				arg: 'timeboxed',
				truthy: true,
			},
		},
	},
	render: args => ({
		components: { SequenceHeader },
		setup() {
			const sequenceExecution = computed(() => ({
				name: args.name,
				state: args.state,
				phase: args.phase,
				timebox: args.timeboxed
					? {
							timeTotal: args.timeTotal,
							timeElapsed: args.timeElapsed,
							isRunningOutOfTime: args.isRunningOutOfTime,
						}
					: undefined,
			}))

			return { sequenceExecution }
		},
		template: '<SequenceHeader :sequenceExecution="sequenceExecution" />',
	}),
} satisfies Meta<StoryArgs>

export default meta
type Story = StoryObj<typeof meta>

export const Waiting: Story = {
	args: {
		name: 'Name of the sequence',
		state: undefined,
		phase: SciconumScenarioExecutionPhase.PENDING,
		timeboxed: false,
	} satisfies StoryArgs,
}

export const Running: Story = {
	args: {
		name: 'Name of the sequence',
		state: State.RUNNING,
		phase: SciconumScenarioExecutionPhase.QUESTION,
		timeboxed: true,
		timeTotal: 30,
		timeElapsed: 15,
		isRunningOutOfTime: false,
	} satisfies StoryArgs,
}

export const Paused: Story = {
	args: {
		name: 'Name of the sequence',
		state: State.PAUSED,
		phase: SciconumScenarioExecutionPhase.QUESTION,
		timeboxed: true,
		timeTotal: 30,
		timeElapsed: 15,
		isRunningOutOfTime: false,
	} satisfies StoryArgs,
}

export const IsRunningOutOfTime: Story = {
	args: {
		name: 'Name of the sequence',
		state: State.RUNNING,
		phase: SciconumScenarioExecutionPhase.QUESTION,
		timeboxed: true,
		timeTotal: 30,
		timeElapsed: 21,
		isRunningOutOfTime: true,
	} satisfies StoryArgs,
}
