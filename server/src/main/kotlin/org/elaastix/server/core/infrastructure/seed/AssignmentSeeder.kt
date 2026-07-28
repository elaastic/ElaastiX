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
import org.elaastix.commons.platform.debt.SciconumTechDebt
import org.elaastix.commons.platform.wip.UnclearAuthorshipOwnership
import org.elaastix.server.assignments.AssignmentEntity
import org.springframework.boot.ApplicationArguments
import org.springframework.core.annotation.Order

@Seeder
@Suppress("MagicNumber")
@Order(3)
class AssignmentSeeder(
	entityManager: EntityManager,
	private val userSeeder: UserSeeder,
	private val sequenceSeeder: SequenceSeeder,
) : AbstractSeeder(entityManager) {

	lateinit var assignment1: AssignmentEntity
		protected set

	@OptIn(SciconumTechDebt::class, UnclearAuthorshipOwnership::class)
	override fun run(args: ApplicationArguments) {
		assignment1 = upsert(
			id = 1UL,
			entity = AssignmentEntity(
				displayName = "Assignment 1",
				sequences = mutableListOf(sequenceSeeder.sequence1, sequenceSeeder.sequence2, sequenceSeeder.sequence3),
				participants = mutableSetOf(userSeeder.student1, userSeeder.student2, userSeeder.student3),
			).apply {
				this.creator = userSeeder.franck
			},
		)
	}
}
