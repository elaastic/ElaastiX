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
// TODO Use TanStack Query would be more appropriate to synchronize a distant state than useState + a watcher
export default defineNuxtPlugin(async () => {
	const useContextApi = () => useApi('/v0/internal/nuxt/context-v1')
	const { data, refresh } = await useContextApi()

	const user = useState<UserAccountDto | null | undefined>(
		STATE_AUTHN_KEY,
		() => undefined,
	)

	watch(
		data,
		(context) => {
			user.value = context?.currentUser
		},
		{
			immediate: true,
		},
	)

	return {
		provide: {
			refreshAuthn: refresh,
		},
	}
})
