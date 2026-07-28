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

import org.elaastix.commons.platform.ExcludeFromCoverage
import org.elaastix.commons.security.ext.RoleHierarchy
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.access.hierarchicalroles.RoleHierarchy

/**
 * Configuration of the *role hierarchy*. See [Authority] for an explainer of the semantics used by Spring Security.
 * See also [Spring Security's documentation](https://docs.spring.io/spring-security/reference/servlet/authorization/architecture.html#authz-hierarchical-roles)
 *
 * `UtilityClassWithPublicConstructor` is suppressed as the bean needs to be static to be loaded before Spring Security.
 * Spring treats companion objects as static members as part of its first-class Kotlin support, but requires a
 * container class to hold the [Configuration] stereotype.
 */
@Configuration
class RoleHierarchyConfiguration {
	companion object {
		@Bean
		fun roleHierarchy() =
			RoleHierarchy {
				Role.ADMIN.implies(Role.WRITER)
				Role.WRITER.implies(Role.USER)
			}
	}

	// This is needed for IntelliJ to actually detect the role hierarchy.
	// Should never be invoked, as the bean should be registered very early in the boot process.
	@Bean
	@ConditionalOnMissingBean(RoleHierarchy::class)
	@ExcludeFromCoverage("Unreachable unless something went seriously wrong")
	fun defaultRoleHierarchy(): RoleHierarchy = error("Missing RoleHierarchy bean?!")
}
