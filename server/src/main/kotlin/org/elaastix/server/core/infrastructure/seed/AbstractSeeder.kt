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
import org.elaastix.commons.platform.wip.UnclearAuthorshipOwnership
import org.elaastix.server.core.AbstractEntityWithAuthorship
import org.elaastix.server.users.entities.UserEntity
import org.springframework.boot.ApplicationRunner

/** Abstract class holding useful logic for all database seeders. */
@Transactional
abstract class AbstractSeeder(private val entityManager: EntityManager) : ApplicationRunner {
	protected fun <T : AbstractEntity> upsert(id: ULong, entity: T): T = upsert0(id, entity)

	@UnclearAuthorshipOwnership
	protected fun <T : AbstractEntityWithAuthorship> upsert(id: ULong, author: UserEntity, entity: T): T =
		upsert0(id, entity) {
			@OptIn(JpaImmutable::class)
			this.author = author
		}

	// TODO: avoid unnecessary UPDATE on startup. dev-only issue, so very low priority.
	private fun <T : AbstractEntity> upsert0(id: ULong, entity: T, block: T.() -> Unit = {}): T =
		entityManager.merge(
			entity.apply {
				@OptIn(JpaImmutable::class) // SAFETY: Test-only identifiers outside the normal allocation range
				this.id = Uuid.fromULongs(0UL, id)

				@OptIn(JpaImmutable::class) // SAFETY: Manual reconciliation of version to make JPA happy
				this.version = entityManager.find(this::class.java, this.id)?.version

				block()
			},
		)
}
