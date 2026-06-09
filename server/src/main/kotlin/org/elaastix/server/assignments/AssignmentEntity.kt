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

package org.elaastix.server.assignments

import jakarta.persistence.Entity
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.elaastix.commons.jpa.entity.AbstractEntity
import org.elaastix.commons.platform.debt.SciconumTechDebt
import org.elaastix.commons.platform.wip.UnclearAuthorshipOwnership
import org.elaastix.server.sequences.SequenceEntity
import org.elaastix.server.users.entities.UserEntity
import org.springframework.data.annotation.CreatedBy

/**
 * An assignment is what learners register/are registered in giving them access to a pedagogical sequence.
 *
 * All learners within the same assignment are "experiencing" the same sequence flow; they're in the same "class"
 * and other learners are their peers. IOW, assignments are hermetic (no learner data is shared across them), but
 * inside a given assignment learners see and interact with each other.
 */
@Entity
class AssignmentEntity(
	/**
	 * The display name given to the assignment.
	 */
	@NotNull
	@Size(min = 2, max = 64)
	var displayName: String,

	/**
	 * The pedagogical sequences this assignment is for.
	 */
	@ManyToMany
	var sequences: MutableList<SequenceEntity>,

	/**
	 * Learners participating in the assignment.
	 */
	@ManyToMany
	@property:SciconumTechDebt(explainer = "We want to make use of UserCohorts instead (maybe?)")
	var participants: MutableSet<UserEntity> = mutableSetOf(),
) : AbstractEntity() {
	/**
	 * The user who created the assignment.
	 */
	@NotNull
	@CreatedBy
	@ManyToOne
	@property:UnclearAuthorshipOwnership // TeacherCohort...
	lateinit var creator: UserEntity
}
