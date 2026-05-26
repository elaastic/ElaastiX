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

package org.elaastix.commons.security

import org.springframework.security.core.GrantedAuthority

/**
 * Trait that defines the *authorities* concept of Spring Security.
 *
 * The terminology used by Spring Security is confusing, due to the liberal use of the term "authority".
 * In the project's source code, we use the term "authority" to refer to *any access control primitive*.
 *
 * An authority is either:
 * - A *role*, which may inherit any number of *authorities*.
 * - A *permission*, which is an atomic RBAC primitive.
 *
 * It is helpful to visualise *authorities* as a trees, where *permissions* are always leaves and *roles* define the
 * actual tree. (In practice it'd be more accurate to see it as a ✨directed acyclic graph✨).
 *
 * @see Role
 * @see Authority
 */
sealed interface Authority : GrantedAuthority {
	override fun getAuthority() =
		when (this) {
			is Role -> "ROLE_$name"
			is Permission -> name
		}
}
