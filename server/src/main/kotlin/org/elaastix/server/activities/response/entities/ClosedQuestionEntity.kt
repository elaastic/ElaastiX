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
import jakarta.persistence.PrePersist
import org.elaastix.mm.content.FormattedText
import org.elaastix.mm.content.RichContent
import org.elaastix.server.activities.response.ClosedAnswer
import org.elaastix.server.users.entities.UserEntity
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

/** A closed question. */
@Entity
@DiscriminatorValue(ClosedQuestionEntity.DISCRIMINATOR)
class ClosedQuestionEntity(
	statement: RichContent,

	/**
	 * The available choices presented to the questionee.
	 * Whether they are shown in order depends on the specific configuration of the activity this question is used in.
	 */
	@JdbcTypeCode(SqlTypes.JSON)
	var choices: List<FormattedText>,

	/** Whether the question accepts multiple answers or not. */
	var multiple: Boolean,

	/** The expected answer. */
	@JdbcTypeCode(SqlTypes.JSON)
	var expectedAnswer: ClosedAnswer,

	answerExplanation: RichContent?,
	author: UserEntity,
) : QuestionEntity(
		statement,
		answerExplanation,
		author,
	) {
	companion object {
		/** Discriminator used within the database. Unique amongst other [QuestionEntity] subclasses. */
		const val DISCRIMINATOR: String = "C"
	}

	@PrePersist
	internal fun checkInvariants() {
		when (val e = expectedAnswer) {
			is ClosedAnswer.Single -> {
				check(!multiple)
				e.value?.let {
					check(it < choices.size.toUInt())
				}
			}

			is ClosedAnswer.Multiple -> {
				check(multiple)
				check(e.value.all { it < choices.size.toUInt() })
			}
		}
	}
}
