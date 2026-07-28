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
import org.elaastix.server.activities.response.entities.QuestionEntity
import org.elaastix.server.scenario.SciconumScenario
import org.elaastix.server.sequences.SciconumSequenceEntity
import org.springframework.boot.ApplicationArguments
import org.springframework.core.annotation.Order

@Seeder
@Suppress("MagicNumber")
@OptIn(UnclearAuthorshipOwnership::class)
@Order(2)
class SequenceSeeder(
	entityManager: EntityManager,
	private val userSeeder: UserSeeder,
	private val questionSeeder: QuestionSeeder,
) : AbstractSeeder(entityManager) {

	@OptIn(SciconumTechDebt::class)
	lateinit var sequence1: SciconumSequenceEntity

	@OptIn(SciconumTechDebt::class)
	lateinit var sequence2: SciconumSequenceEntity

	@OptIn(SciconumTechDebt::class)
	lateinit var sequence3: SciconumSequenceEntity

	@OptIn(SciconumTechDebt::class)
	override fun run(args: ApplicationArguments) {
		val questions = mutableListOf<QuestionEntity>(questionSeeder.tmpQuestion1, questionSeeder.tmpQuestion2)
		sequence1 = upsert(
			id = 1UL,
			entity = SciconumSequenceEntity(
				name = "Sequence 1",
				sciconumScenario = SciconumScenario.CONTROL,
				sciconumQuestions = questions,
			).apply {
				this.owner = userSeeder.franck
			},
		)

		sequence2 = upsert(
			id = 2UL,
			entity = SciconumSequenceEntity(
				name = "Sequence 2",
				sciconumScenario = SciconumScenario.PEER_ASSESSMENT,
				sciconumQuestions = questions,
			).apply {
				this.owner = userSeeder.franck
			},
		)

		sequence3 = upsert(
			id = 3UL,
			entity = SciconumSequenceEntity(
				name = "Sequence 3",
				sciconumScenario = SciconumScenario.PEER_DEBATE,
				sciconumQuestions = questions,
			).apply {
				this.owner = userSeeder.franck
			},
		)
	}
}
