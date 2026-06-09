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

package org.elaastix.server.core.infrastructure.config

import org.elaastix.server.users.entities.UserEntity
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.security.core.context.SecurityContextHolder
import java.util.Optional

/**
 * Configuration for Spring Data's JPA auditing.
 * See https://docs.spring.io/spring-data/jpa/reference/auditing.html#auditing.auditor-aware
 */
@Configuration
@EnableJpaAuditing
class JpaAuditConfig : AuditorAware<UserEntity> {
	override fun getCurrentAuditor(): Optional<UserEntity> =
		Optional.ofNullable(
			SecurityContextHolder.getContext().authentication
				?.takeIf { it.isAuthenticated }
				?.let { it.principal as? UserEntity? },
		)
}
