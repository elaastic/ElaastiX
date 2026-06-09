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
import org.elaastix.commons.security.Role
import org.elaastix.server.users.entities.UserEntity
import org.springframework.boot.ApplicationArguments
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order

@Seeder
@Order(Ordered.HIGHEST_PRECEDENCE)
class UserSeeder(entityManager: EntityManager) : AbstractSeeder(entityManager) {
	lateinit var franck: UserEntity
		protected set

	lateinit var john: UserEntity
		protected set

	lateinit var cynthia: UserEntity
		protected set

	override fun run(args: ApplicationArguments) {
		franck = upsert(
			id = 1UL,
			entity = UserEntity(
				firstName = "Franck",
				lastName = "Silvestre",
				email = "franck.silvestre@elaastix.example",
				roles = setOf(Role.ADMIN),
			),
		)

		john = upsert(
			id = 2UL,
			entity = UserEntity(
				firstName = "John",
				lastName = "Tranier",
				email = "john.tranier@elaastix.example",
				roles = setOf(Role.WRITER),
			),
		)

		cynthia = upsert(
			id = 3UL,
			entity = UserEntity(
				firstName = "Cynthia",
				lastName = "Rey",
				email = "cynthia.rey@elaastix.example",
				roles = setOf(Role.USER),
			),
		)
	}
}
