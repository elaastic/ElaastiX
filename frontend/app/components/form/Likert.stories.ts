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

import Likert, { LikertScaleType } from './Likert.vue'

const meta = {
	title: 'Form/Likert',
	component: Likert,
	tags: ['autodocs'],
	// Controls generation sucks in monorepos...
	argTypes: {
		type: {
			control: 'select',
			options: Object.values(LikertScaleType),
		},
		points: {
			control: {
				type: 'range',
				min: 3,
				max: 7,
			},
		},
	},
} satisfies Meta<typeof Likert>

export default meta
type Story = StoryObj<typeof meta>

export const AgreementLikert: Story = {
	args: {
		type: LikertScaleType.AGREEMENT,
		points: 5,
	},
}

export const ConfidenceLikert: Story = {
	args: {
		type: LikertScaleType.CONFIDENCE,
		points: 4,
	},
}
