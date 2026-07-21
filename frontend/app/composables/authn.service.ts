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

import type { AsyncDataExecuteOptions, AsyncDataRequestStatus } from '#app/composables/asyncData'

export const STATE_AUTHN_KEY = 'authn'

export type UserAuthenticated = {
	user: Ref<UserAccountDto | null | undefined>
	displayUser: ComputedRef<{
		name: string
		roles: string
	}>
	isAuthenticated: ComputedRef<boolean>
	refresh: (opts?: AsyncDataExecuteOptions) => Promise<void>
	status: Ref<AsyncDataRequestStatus>
}

const useContextController = () => useApi('/v0/internal/nuxt/context-v1')

export function initAuthnContext() {
	const currentUser = useState<UserAccountDto | null | undefined>(STATE_AUTHN_KEY, () => undefined)
	const { data } = useContextController()

	watch(data, (context) => {
		currentUser.value = context?.currentUser
	}, { immediate: true })
}

export function useAuthnContext(): UserAuthenticated {
	const { t } = useI18n()
	const { refresh, status } = useContextController()
	const currentUser = useState<UserAccountDto | null | undefined>(STATE_AUTHN_KEY)
	const isAuthenticated = computed(() => !!currentUser.value)

	const displayUser = computed(() => ({
		name: currentUser.value?.firstname ?? t('login.guest'),
		roles: currentUser.value?.roles.join(', ') ?? '',
	}))

	return {
		user: currentUser,
		displayUser,
		isAuthenticated,
		refresh,
		status,
	}
}

export async function useAwaitAuthnContext(): Promise<UserAuthenticated> {
	const { refresh, status, data } = await useContextController()
	const user = ref(data.value?.currentUser)
	const isAuthenticated = computed(() => (user.value !== null && user.value !== undefined))
	const displayUser = computed(() => ({
		name: user.value?.firstname ?? '',
		roles: user.value?.roles.join(', ') ?? '',
	}))

	return Promise.resolve({
		user,
		displayUser,
		isAuthenticated,
		refresh,
		status,
	})
}
