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
import org.elaastix.mm.content.FormattedContent
import org.elaastix.mm.content.RichContent
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

/** An open question. */
@Entity
@DiscriminatorValue(OpenQuestionEntity.DISCRIMINATOR)
class OpenQuestionEntity(
	statement: RichContent,

	/**
	 * The expected answer.
	 *
	 * Simply a formatted text, to match the format allowed in responses which is restricted for security reasons.
	 * Richer text should go to [answerExplanation] and serve as additional resources.
	 *
	 * It would be unfair to expect an answer with formatting that the learners are unable to use; it forces the
	 * question author to put themselves in the student's shoes while preparing the questions.
	 */
	@JdbcTypeCode(SqlTypes.JSON)
	@Suppress("JpaAttributeTypeInspection") // https://youtrack.jetbrains.com/issue/IDEA-191568
	var expectedAnswer: FormattedContent,

	answerExplanation: RichContent?,
) : QuestionEntity(
	statement,
	answerExplanation,
) {
	companion object {
		/** Discriminator used within the database. Unique amongst other [QuestionEntity] subclasses. */
		const val DISCRIMINATOR: String = "O"
	}
}
