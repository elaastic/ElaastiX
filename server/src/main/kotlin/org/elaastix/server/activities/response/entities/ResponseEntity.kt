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

import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import org.elaastix.commons.jpa.AbstractEntity
import org.elaastix.mm.activity.ScalarGradable
import org.elaastix.mm.content.FormattedContent
import org.elaastix.server.users.entities.UserEntity
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

/**
 * Generic response. Lacks important properties that are defined by subclasses.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
abstract class ResponseEntity<TSelf : ResponseEntity<TSelf, TQuestion, TAnswer>, TQuestion : QuestionEntity, TAnswer>(
	/** The question this response is attached to. */
	@ManyToOne(targetEntity = QuestionEntity::class)
	var question: TQuestion,

	/** The answer given by the learner. May be empty, but is required. */
	@JdbcTypeCode(SqlTypes.JSON)
	var answer: TAnswer,

	/** Self explanation provided by the learner. Optional. */
	@JdbcTypeCode(SqlTypes.JSON)
	var selfExplanation: FormattedContent?,

	/** Confidence degree provided by the learner. Optional. */
	var confidenceDegree: UInt?,

	/** The author of this response. */
	@ManyToOne
	var author: UserEntity,

	/**
	 * The response being amended by this response.
	 *
	 * If a learner has already answered a question, but later decides to change their answer as they're invited to
	 * do so, the new response "amends" the previous one.
	 *
	 * This allows tracking the revision history for a question, and when in the learning path have these responses
	 * been created.
	 */
	@OneToOne(targetEntity = ResponseEntity::class)
	var amendedResponse: TSelf? = null,

	@Embedded
	override var absoluteGrade: ScalarGradable.ScalarGrade? = null,
) : AbstractEntity(),
	ScalarGradable
