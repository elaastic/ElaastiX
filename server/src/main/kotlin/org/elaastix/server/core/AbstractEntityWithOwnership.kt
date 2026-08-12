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

import jakarta.persistence.FetchType
import jakarta.persistence.ManyToOne
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.PrePersist
import jakarta.validation.constraints.NotNull
import org.elaastix.commons.jpa.entity.AbstractEntity
import org.elaastix.server.users.entities.UserEntity
import org.springframework.data.annotation.CreatedBy

/**
 * Trait for entities that keep track of ownership.
 * The creator is the user that actually created the entity.
 * The owner is the user allowed to manage the entity. If not specified, the creator is assumed to be the owner.
 */
@MappedSuperclass
abstract class AbstractEntityWithOwnership(owner: UserEntity? = null) : AbstractEntity() {
	/**
	 * Owner of the resource, but not necessarily its author per se.
	 * The owner is allowed to manage the resource.
	 */
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	lateinit var owner: UserEntity

	/**
	 * The user that has actually created the resource (not being necessarily the same as the owner).
	 */
	@NotNull
	@CreatedBy
	@ManyToOne(fetch = FetchType.LAZY)
	lateinit var creator: UserEntity

	/**
	 * Sets the owner to the creator if not already set.
	 */
	@PrePersist
	fun initializeOwner() {
		if (!::owner.isInitialized) {
			owner = creator
		}
	}

	init {
		owner?.let { this.owner = it }
	}
}
