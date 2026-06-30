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
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import org.elaastix.mm.activity.AbsoluteGradable
import org.elaastix.mm.activity.ScalarGrade
import org.elaastix.mm.content.FormattedContent
import org.elaastix.server.activities.response.ClosedAnswer
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.jetbrains.annotations.NotNull

/** Answer to a [ClosedQuestionEntity]. */
@Entity
@DiscriminatorValue(ClosedResponseEntity.DISCRIMINATOR)
class ClosedResponseEntity(
	question: ClosedQuestionEntity,

	/** The answer given by the learner. May be empty, but is required. */
	@NotNull
	@JdbcTypeCode(SqlTypes.JSON)
	var answer: ClosedAnswer,

	selfExplanation: FormattedContent?,
	confidenceDegree: UInt?,
	amendedResponse: ClosedResponseEntity? = null,
	scalarGrade: ScalarGrade? = null,

	@Enumerated(EnumType.STRING)
	override val absoluteGrade: AbsoluteGradable.AbsoluteGrade? = null,
) : ResponseEntity<ClosedResponseEntity, ClosedQuestionEntity>(
	question,
	selfExplanation,
	confidenceDegree,
	amendedResponse,
	scalarGrade,
),
	AbsoluteGradable {
	companion object {
		/** Discriminator used within the database. Unique amongst other [ResponseEntity] subclasses. */
		const val DISCRIMINATOR: String = "C"
	}
}
