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

package org.elaastix.commons.jpa.repository

import io.hypersistence.utils.spring.repository.BaseJpaRepository
import org.elaastix.commons.jpa.entity.AbstractEntity
import org.springframework.data.repository.NoRepositoryBean
import kotlin.uuid.Uuid

/**
 * Specialised Repository type for use in all Elaastix projects.
 * Wrapper around Hypersistence Utils's [BaseJpaRepository].
 *
 * @param T Type of entities managed by the repository. Must be a subclass of [AbstractEntity].
 */
@NoRepositoryBean
interface ElaastixRepository<T : AbstractEntity> : BaseJpaRepository<T, Uuid> {
	/**
	 * Helper to use Kotlin nullability instead of Java's `Optional`, which is more idiomatic.
	 */
	fun findByIdOrNull(id: Uuid): T? = findById(id).orElse(null)

	/**
	 * Helper to get the concrete class of an entity by its ID.
	 * Useful to avoid fetching the entire entity when only interested in its type.
	 */
	fun findConcreteTypeById(id: Uuid): Class<T>?

	/**
	 * Helper to get an entity reference usable for creating entities with relations.
	 *
	 * The actual entity object will not be fetched, unless a property other than its `id` is read.
	 * A request to the database will be made to query the entity's concrete type.
	 *
	 * The output will be the narrowest subclass of [T] (or [T] itself).
	 * Returns `null` if the entity does not exist.
	 *
	 * @see getReferenceById
	 * @see getTypedReferenceById
	 */
	fun getConcreteReferenceById(id: Uuid): T?

	/**
	 * Like [getReferenceById], but with the option to use a more narrow return type.
	 *
	 * @see getReferenceById
	 * @see getConcreteReferenceById
	 */
	fun <U : T> getTypedReferenceById(clazz: Class<U>, id: Uuid): U

	/**
	 * Finds the entity by its ID, applies some changes, and updates it.
	 */
	fun findByIdAndUpdate(id: Uuid, block: T.() -> Unit): T?
}

/** Reified helper method. */
inline fun <T : AbstractEntity, reified U : T> ElaastixRepository<T>.getTypedReferenceById(id: Uuid) =
	getTypedReferenceById(U::class.java, id)
