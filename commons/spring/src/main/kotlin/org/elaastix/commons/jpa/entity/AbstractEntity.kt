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

package org.elaastix.commons.jpa.entity

import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Version
import jakarta.validation.constraints.NotNull
import org.elaastix.commons.platform.JpaImmutable
import kotlin.time.Instant

/**
 * Abstract class holding common properties shared by all entities.
 * Deals with the implementation of best current practice, specifically around [equals] and [hashCode].
 *
 * All entities within Elaastix SHOULD inherit from AbstractEntity.
 */
@MappedSuperclass
@EntityListeners(EntityListener::class)
abstract class AbstractEntity : AbstractMinimalEntity() {
	/**
	 * Date of last modification of the entity (millisecond precision).
	 * Equal to the creation timestamp for newly created entities.
	 */
	@NotNull
	var updatedAt: Instant = createdAt
		@JpaImmutable set

	/**
	 * The version of the database record.
	 */
	@Version
	@NotNull
	var version: Long? = null
		@JpaImmutable set
}
