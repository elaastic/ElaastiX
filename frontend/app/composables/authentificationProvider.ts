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

import type {AsyncDataExecuteOptions} from "#app/composables/asyncData";

export type UserAuthenticated = {
	userAuthenticated: ComputedRef<UserAccountDto | null | undefined>;
	refresh: (opts?: AsyncDataExecuteOptions) => Promise<void>;
};

const key = Symbol() as InjectionKey<UserAuthenticated>

export function createAuthenticationContext() {
	const {data, refresh} = useApi('/v0/internal/nuxt/context-v1', {
		watch: false,
	})

	const userAuthenticated = computed<UserAccountDto | null | undefined>(() => data.value?.currentUser)

	provide(key, {
		userAuthenticated,
		refresh
	})
}

export function provideAuthentification(): UserAuthenticated {
	return inject(key)!
}
