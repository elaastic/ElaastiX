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

package org.elaastix.server.core.infrastructure.seed

import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.elaastix.commons.data.Uuid
import org.elaastix.commons.jpa.entity.AbstractEntity
import org.elaastix.commons.platform.JpaImmutable
import org.elaastix.server.core.AbstractEntityWithAuthorship
import org.elaastix.server.users.entities.UserEntity
import org.springframework.boot.ApplicationRunner

/** Abstract class holding useful logic for all database seeders. */
@Transactional
abstract class AbstractSeeder(private val entityManager: EntityManager) : ApplicationRunner {
	private var allocatedIds = 0UL

	protected fun <T : AbstractEntity> createEntity(entity: T): T = createEntity0(entity)

	protected fun <T : AbstractEntityWithAuthorship> createEntityWithAuthor(author: UserEntity, entity: T): T =
		createEntity0(entity) {
			@OptIn(JpaImmutable::class)
			this.author = author
		}

	private fun <T : AbstractEntity> createEntity0(entity: T, block: T.() -> Unit = {}): T =
		entityManager.merge(
			entity.apply {
				@OptIn(JpaImmutable::class) // SAFETY: Test-only identifiers outside the normal allocation range
				id = Uuid.fromULongs(0UL, ++allocatedIds)

				block()

				// TODO: avoid unnecessary UPDATE on startup. dev-only issue, so very low priority.
				@OptIn(JpaImmutable::class) // SAFETY: Pulled from the database, allows merge to work efficiently
				entityManager.find(this::class.java, id)?.let {
					version = it.version
					updatedAt = it.updatedAt
				}
			},
		)
}
