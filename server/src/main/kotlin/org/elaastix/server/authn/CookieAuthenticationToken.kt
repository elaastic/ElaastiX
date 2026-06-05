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

package org.elaastix.server.authn

import jakarta.servlet.http.Cookie
import org.elaastix.commons.data.Uuid
import org.elaastix.commons.data.UuidSerializer.toStringBase36
import org.springframework.security.core.GrantedAuthority

/**
 * Authentication token for cookie-based authentication.
 *
 * @property cookie The cookie sent by the client
 * @property userId The user ID contained in the cookie
 */
class CookieAuthenticationToken(val cookie: Cookie, val userId: Uuid) : AuthToken() {
	override fun getAuthorities() = emptySet<GrantedAuthority>()

	override fun getCredentials() = cookie

	override fun getDetails() = null

	override fun getPrincipal() = userId

	override fun isAuthenticated() = false

	override fun setAuthenticated(authenticated: Boolean) =
		throw UnsupportedOperationException("Cannot change the authentication status of a token")

	override fun getName() = userId.toStringBase36()
}
