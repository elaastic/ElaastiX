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
import type { RouteLocationRaw } from 'vue-router'

export const STATE_AUTHN_KEY = 'authn'

export function useAuthn() {
	const { $i18n, $api } = useNuxtApp()
	const currentUser = useState<UserAccountDto | null | undefined>(STATE_AUTHN_KEY)
	const isAuthenticated = computed(() => !!currentUser.value)

	const displayUser = computed(() => ({
		name: currentUser.value?.firstname ?? $i18n.t('login.guest'),
		roles: currentUser.value?.roles.join(', ') ?? '',
	}))

	const contextApi = useApi(
		'/v0/internal/nuxt/context-v1',
		{
			immediate: false,
		},
	)

	async function refresh() {
		await contextApi.execute()
		currentUser.value = contextApi.data.value?.currentUser
	}

	async function login(userId: string, redirectTo: string = '/') {
		await $api(`/v1/authn/tmp/login`, {
			method: 'POST',
			headers: {
				Authorization: `Develop ${userId}`,
			},
		})

		await refresh()
		navigateTo(redirectTo)
	}

	async function logout(to: RouteLocationRaw = '/') {
		await $api('/v1/authn/tmp/logout', {
			method: 'DELETE',
		})
		currentUser.value = null
		navigateTo(to)
	}

	return {
		user: currentUser,
		displayUser,
		isAuthenticated,
		logout,
		login,
		refresh,
	}
}
