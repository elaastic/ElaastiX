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
import org.elaastix.server.scenario.exec.SciconumScenarioExecutionPhase
import org.elaastix.server.scenario.exec.entities.SciconumScenarioSessionEntity
import org.springframework.boot.ApplicationArguments
import org.springframework.core.annotation.Order

@Seeder
@Suppress("MagicNumber")
@Order(4)
class SciconumScenarioSessionSeeder(
	entityManager: EntityManager,
	private val assignmentSeeder: AssignmentSeeder,
	private val sequenceSeeder: SequenceSeeder,
) : AbstractSeeder(entityManager) {

	@OptIn(SciconumTechDebt::class)
	lateinit var scenarioSession1: SciconumScenarioSessionEntity
		protected set

	@OptIn(SciconumTechDebt::class)
	override fun run(args: ApplicationArguments) {
		scenarioSession1 = upsert(
			id = 1UL,
			entity = SciconumScenarioSessionEntity(
				assignment = assignmentSeeder.assignment1,
				sequence = sequenceSeeder.sequence1,
				currentRound = 0U,
				phase = SciconumScenarioExecutionPhase.PENDING,
			),
		)
	}
}
