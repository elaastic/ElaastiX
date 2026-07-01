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

import jakarta.persistence.EntityManager
import org.elaastix.commons.jpa.entity.AbstractMinimalEntity
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory
import org.springframework.data.repository.core.RepositoryInformation
import org.springframework.data.repository.core.RepositoryMetadata

/**
 * Factory class responsible for creating instances of [ElaastixRepositoryImpl] with the event publisher.
 */
class ElaastixRepositoryImplFactory(entityManager: EntityManager) : JpaRepositoryFactory(entityManager) {
	@Suppress("UNCHECKED_CAST")
	override fun getTargetRepository(information: RepositoryInformation, entityManager: EntityManager) =
		ElaastixRepositoryImpl(
			getEntityInformation(information.domainType as Class<AbstractMinimalEntity>),
			entityManager,
		)

	override fun getRepositoryBaseClass(metadata: RepositoryMetadata) = ElaastixRepositoryImpl::class.java
}
