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

package org.elaastix.server.assignments.dto

import kotlinx.serialization.Serializable
import org.elaastix.commons.data.Uuid
import org.elaastix.commons.platform.debt.SciconumTechDebt
import org.elaastix.server.sequences.dto.SequenceDto

@Serializable
data class AssignmentDto(
	/** The assignment's unique identifier. */
	val id: Uuid,

	/** The display name given to the assignment.*/
	val displayName: String,

	/** The pedagogical sequences this assignment is for.*/
	@property:SciconumTechDebt(explainer = "Unsure we want to ship all sequences here. Flagging as 'to review later'.")
	val sequences: List<SequenceDto>,

	/** Learners participating in the assignment.*/
	val participantIds: Set<Uuid>,

	/** The user who created the assignment.*/
	val creatorId: Uuid,
)
