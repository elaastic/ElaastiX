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

package org.elaastix.commons.jpa

import io.hypersistence.utils.spring.repository.BaseJpaRepositoryImpl
import jakarta.persistence.EntityManager
import org.elaastix.commons.data.Uuid
import org.springframework.data.jpa.repository.support.JpaEntityInformation

/**
 * Concrete repository implementation of [ElaastixRepository].
 * Picked up by Spring Data while setting things up.
 */
class ElaastixRepositoryImpl<T : AbstractEntity>(
	private val entityInformation: JpaEntityInformation<T, *>,
	private val entityManager: EntityManager,
) : BaseJpaRepositoryImpl<T, Uuid>(entityInformation, entityManager),
	ElaastixRepository<T> {
	override fun findConcreteTypeById(id: Uuid): Class<T>? {
		val cb = entityManager.criteriaBuilder
		val cq = cb.createQuery(Class::class.java)

		val root = cq.from(entityInformation.javaType)
		val pk = root.get(entityInformation.idAttribute)

		cq.select(root.type())
		cq.where(pk.equalTo(id))

		@Suppress("UNCHECKED_CAST") // SAFETY: Should be guaranteed by the `from` clause.
		return entityManager.createQuery(cq).singleResultOrNull as Class<T>?
	}

	override fun getEntityReference(id: Uuid): T = entityManager.getReference(entityInformation.javaType, id)

	override fun getConcreteEntityReference(id: Uuid): T? {
		val clazz = findConcreteTypeById(id)
		return clazz?.let { entityManager.getReference(it, id) }
	}

	override fun <U : T> getEntityReferenceWithType(id: Uuid, clazz: Class<U>): U =
		entityManager.getReference(clazz, id)
}
