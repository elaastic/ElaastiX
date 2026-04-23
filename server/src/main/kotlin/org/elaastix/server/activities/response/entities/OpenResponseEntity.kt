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

package org.elaastix.server.activities.response.entities

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import org.elaastix.mm.activity.ScalarGradable
import org.elaastix.mm.content.FormattedContent
import org.elaastix.server.users.entities.UserEntity
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

/** Answer to a [OpenQuestionEntity]. */
@Entity
@DiscriminatorValue(OpenResponseEntity.DISCRIMINATOR)
class OpenResponseEntity(
	@ManyToOne
	override val question: OpenQuestionEntity,

	/** The answer provided by the learner. */
	@JdbcTypeCode(SqlTypes.JSON)
	val answer: FormattedContent,

	selfExplanation: FormattedContent?,
	confidenceDegree: UInt?,
	author: UserEntity,

	@OneToOne
	override val amendedResponse: OpenResponseEntity? = null,

	absoluteGrade: ScalarGradable.ScalarGrade? = null,
) : ResponseEntity(
		question,
		selfExplanation,
		confidenceDegree,
		author,
		amendedResponse,
		absoluteGrade,
	) {
	companion object {
		/** Discriminator used within the database. Unique amongst other [ResponseEntity] subclasses. */
		const val DISCRIMINATOR: String = "O"
	}
}
