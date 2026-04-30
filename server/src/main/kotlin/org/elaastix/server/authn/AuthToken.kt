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

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

/** Abstract class for authentication tokens. */
abstract class AuthToken : Authentication {
	override fun getAuthorities() = emptySet<GrantedAuthority>()

	override fun getDetails() = null

	override fun isAuthenticated() = false

	override fun setAuthenticated(authenticated: Boolean) =
		throw UnsupportedOperationException("Cannot change the authentication status of a token")

	override fun getName() = principal?.toString()

	override fun equals(other: Any?): Boolean =
		other is AuthToken &&
			principal == other.principal &&
			credentials == other.credentials

	override fun hashCode(): Int {
		var code = 31
		if (principal != null) code = code xor principal.hashCode()
		if (credentials != null) code = code xor credentials.hashCode()
		return code
	}

	override fun toString(): String {
		val creds = if (credentials == null) "null" else "<hidden>"
		return "${this::class.java.name}(principal=$principal, credentials=$creds)"
	}
}
