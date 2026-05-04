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

import org.elaastix.commons.data.Uuid
import org.springframework.security.authentication.InsufficientAuthenticationException
import org.springframework.security.core.GrantedAuthority

/**
 * Authentication token for the `Develop` authentication method. Only usable in dev.
 *
 * The authentication provider may fail with [InsufficientAuthenticationException] if it deems the token unsuitable,
 * such as when not running in development.
 */
class DevelopAuthenticationToken(
	/** The UUID to identify as. */
	val uuid: Uuid,
) : AuthToken() {
	override fun getAuthorities() = emptySet<GrantedAuthority>()

	// There's never credentials in develop, it just lets you in w/o checks
	override fun getCredentials() = null

	override fun getDetails() = null

	override fun getPrincipal() = uuid

	override fun isAuthenticated() = false

	override fun setAuthenticated(authenticated: Boolean) =
		throw UnsupportedOperationException("Cannot change the authentication status of a token")

	override fun getName(): String? {
		TODO("Not yet implemented")
	}
}
