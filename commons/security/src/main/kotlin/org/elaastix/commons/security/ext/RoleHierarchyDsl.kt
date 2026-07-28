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

@file:Suppress("FunctionNaming", "FunctionName")

package org.elaastix.commons.security.ext

import org.elaastix.commons.security.Role
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl

@Suppress("UndocumentedPublicClass", "UndocumentedPublicFunction", "SpreadOperator")
class RoleHierarchyDsl(private var builder: RoleHierarchyImpl.Builder) {
	fun Role.implies(vararg roles: Role) {
		builder = builder.role(name).implies(*roles.map { it.name }.toTypedArray())
	}
}

/**
 * DSL to create a role hierarchy with idiomatic Kotlin.
 */
fun RoleHierarchy(roleHierarchyConfiguration: RoleHierarchyDsl.() -> Unit): RoleHierarchyImpl =
	RoleHierarchyImpl.withDefaultRolePrefix()
		.also { RoleHierarchyDsl(it).apply(roleHierarchyConfiguration) }
		.build()
