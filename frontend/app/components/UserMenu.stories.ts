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
import { useState } from '#app'

import UserMenu from './UserMenu.vue'
import { STATE_AUTHN_KEY } from '~/composables/authn.service'

const meta = {
	title: 'Sidebar/UserMenu',
	component: UserMenu,
	tags: ['autodocs'],
} satisfies Meta<typeof UserMenu>

export default meta
type Story = StoryObj<typeof meta>

export const Logout: Story = {
	args: {},
	decorators: [
		() => ({
			setup() {
				const user = useState<UserAccountDto | null | undefined>(STATE_AUTHN_KEY)
				user.value = undefined
			},
			template: '<story />',
		}),
	],
}

export const Login: Story = {
	args: {},
	decorators: [
		() => ({
			setup() {
				const user = useState<UserAccountDto | null | undefined>(STATE_AUTHN_KEY)
				user.value = {
					id: 'ee',
					firstname: 'Franck',
					lastname: 'Silvestre',
					email: 'franck.silvestre.com',
					roles: ['ADMIN', 'WRITER'],
				} satisfies UserAccountDto
			},
			template: '<story />',
		}),
	],
}
