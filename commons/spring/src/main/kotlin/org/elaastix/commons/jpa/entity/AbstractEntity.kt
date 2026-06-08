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
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Transient
import jakarta.persistence.Version
import jakarta.validation.constraints.NotNull
import org.elaastix.commons.data.Uuid
import org.elaastix.commons.platform.JpaImmutable
import org.hibernate.proxy.HibernateProxy
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi

/**
 * Abstract class holding common properties shared by all entities.
 * Deals with the implementation of best current practice, specifically around [equals] and [hashCode].
 *
 * All entities within Elaastix SHOULD inherit from AbstractEntity.
 */
@MappedSuperclass
@EntityListeners(EntityListener::class)
abstract class AbstractEntity {
	/**
	 * ID of the entity. The ID is a UUID v7 as specified by [RFC 9562](https://www.rfc-editor.org/rfc/rfc9562.html).
	 */
	@Id
	@OptIn(ExperimentalUuidApi::class)
	var id: Uuid = Uuid.generateV7()
		@JpaImmutable set

	/**
	 * Date of creation of the entity (millisecond precision).
	 * Derived from the [id], as UUID v7 encodes the creation timestamp.
	 *
	 * For querying the creation timestamp from the database, please refer to Postgres 18's
	 * [`uuid_extract_timestamp`](https://www.postgresql.org/docs/18/functions-uuid.html#FUNC_UUID_EXTRACT_TABLE).
	 */
	@delegate:Transient
	val createdAt by lazy {
		val timestamp = (id.toLongs { msb, _ -> msb ushr 16 }) and 0xFFFFFFFFFFFFL
		Instant.fromEpochMilliseconds(timestamp)
	}

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

	/**
	 * Checks if the entity is equal to [other].
	 * Equality is based on the entity's class and identifier.
	 *
	 * Details of the implementation have been researched by the JPA Buddy team (now part of the JetBrains ecosystem).
	 * It is specifically designed to be aware of Hibernate shenanigans, to avoid at all costs triggering side effects.
	 */
	final override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other == null) return false
		val oEffectiveClass = other.hibernateAwareJavaClass
		val thisEffectiveClass = this.hibernateAwareJavaClass
		if (thisEffectiveClass != oEffectiveClass) return false

		// SAFETY: Cast is safe; verified the class type.
		return id == (other as AbstractEntity).id
	}

	/**
	 * Computes the hash for the entity.
	 *
	 * The hash is solely determined by the ID, which is safe as we use globally unique IDs,
	 * and allocate it immediately upon entity creation ensuring stability of the result hash.
	 */
	final override fun hashCode(): Int = id.hashCode()

	@get:Transient
	private val Any.hibernateAwareJavaClass: Class<*>
		get() = if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
}
