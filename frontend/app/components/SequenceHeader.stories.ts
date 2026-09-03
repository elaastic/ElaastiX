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

const meta = {
	title: 'Sequence Header View',
	component: SequenceHeader,
	tags: ['autodocs'],
} satisfies Meta<typeof SequenceHeader>

export default meta
type Story = StoryObj<typeof meta>

export const Waiting: Story = {
	args: {
		name: 'Name of the sequence',
		state: undefined,
		phase: SciconumScenarioExecutionPhase.PENDING,
		totalTime: 0,
		timeSpend: 0,
		lastingTimeString: '',
		lessThan10secForCurrentSeq: false,
	},
}

export const Running: Story = {
	args: {
		name: 'Name of the sequence',
		state: State.RUNNING,
		phase: SciconumScenarioExecutionPhase.QUESTION,
		totalTime: 30,
		timeSpend: 15,
		lastingTimeString: '45s',
		lessThan10secForCurrentSeq: false,
	},
}

export const Paused: Story = {
	args: {
		name: 'Name of the sequence',
		state: State.PAUSED,
		phase: SciconumScenarioExecutionPhase.QUESTION,
		totalTime: 30,
		timeSpend: 15,
		lastingTimeString: '45s',
		lessThan10secForCurrentSeq: false,
	},
}

export const LessThan10Sec: Story = {
	args: {
		name: 'Name of the sequence',
		state: State.RUNNING,
		phase: SciconumScenarioExecutionPhase.QUESTION,
		totalTime: 30,
		timeSpend: 21,
		lastingTimeString: '9s',
		lessThan10secForCurrentSeq: true,
	},
}
