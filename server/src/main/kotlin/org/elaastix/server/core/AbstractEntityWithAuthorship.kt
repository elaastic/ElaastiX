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

package org.elaastix.server.core

import jakarta.persistence.ManyToOne
import jakarta.persistence.MappedSuperclass
import org.elaastix.commons.jpa.entity.AbstractEntity
import org.elaastix.commons.platform.JpaImmutable
import org.elaastix.server.users.entities.UserEntity
import org.springframework.data.annotation.CreatedBy

/**
 * Trait for entities that keep track of authorship.
 * Leverages Spring Data's auditing infrastructure and Spring Security to receive the current user.
 *
 * TODO: Improve and implement authorship tracking via an audit trail
 */
@MappedSuperclass
abstract class AbstractEntityWithAuthorship : AbstractEntity() {
	/**
	 * The original author.
	 */
	@CreatedBy
	@ManyToOne
	lateinit var author: UserEntity
		@JpaImmutable set
}
