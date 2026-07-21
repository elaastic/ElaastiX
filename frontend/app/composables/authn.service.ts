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

export const STATE_AUTHN_KEY = 'authn'

const useContextController = () => useApi('/v0/internal/nuxt/context-v1')

export function initAuthnContext() {
	const currentUser = useState<UserAccountDto | null | undefined>(STATE_AUTHN_KEY, () => undefined)
	const { data } = useContextController()

	watch(data, (context) => {
		currentUser.value = context?.currentUser
	}, { immediate: true })
}

export function useAuthnContext() {
	const { t } = useI18n()
	const { refresh } = useContextController()
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
	}
}
