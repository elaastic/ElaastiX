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

import org.elaastix.server.users.entities.UserEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

/** Authentication object tailored to the Elaastix platform. */
class ElaastixAuthentication(
	private val user: UserEntity,
	private val credentials: Any?,
	private var authenticated: Boolean = false,
) : Authentication {

	override fun getPrincipal(): UserEntity = user

	override fun getCredentials() = credentials

	override fun getName() = user.id.toString() // FIXME: user's actual name!!

	// Unused
	override fun getAuthorities() = emptySet<GrantedAuthority>()

	// Unused
	override fun getDetails() = null

	override fun isAuthenticated(): Boolean = authenticated

	override fun setAuthenticated(isAuthenticated: Boolean) {
		authenticated = isAuthenticated
	}
}
