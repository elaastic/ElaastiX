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

package org.elaastix.server.sequences.dto

import kotlinx.serialization.Serializable
import org.elaastix.commons.data.Uuid
import org.elaastix.commons.platform.debt.SciconumTechDebt
import org.elaastix.commons.platform.wip.UnclearAuthorshipOwnership
import org.elaastix.server.activities.response.dtos.QuestionStatementDto
import org.elaastix.server.scenario.SciconumScenario

@Serializable
@Suppress("UndocumentedPublicClass") // TODO
data class SequenceDto @SciconumTechDebt constructor(
	/** The sequence's unique identifier. */
	val id: Uuid,

	/** Display name of the sequence. */
	val name: String,

	/** SCICONUM scenario of the sequence. */
	@property:SciconumTechDebt
	val sciconumScenario: SciconumScenario,

	/** Questions for the SCICONUM sequence. */
	@property:SciconumTechDebt
	val sciconumQuestions: Iterable<QuestionStatementDto>,

	/** Identifier of the scenario owner. */
	@property:UnclearAuthorshipOwnership
	val ownerId: Uuid,
)
