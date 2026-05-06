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

package org.elaastix.server.activities.response.entities.projections

import org.elaastix.commons.data.Uuid
import org.elaastix.mm.content.FormattedText
import org.elaastix.mm.content.RichContent
import org.elaastix.server.activities.response.entities.ClosedQuestionEntity
import org.elaastix.server.activities.response.entities.OpenQuestionEntity
import org.elaastix.server.activities.response.entities.QuestionEntity

// One would think some of the documentation is tautologic, especially when it's just `@see [...]`.
// But it's actually a nice way to see the identifiers match, which is actually important for the projection.

/**
 * Projection class with only the question's statement and, for closed questions, the list of possible answers.
 *
 * This type is meant as a transition layer between the DAO and the service layer. As soon as this object is
 * received, you should transform it in a more suitable type.
 */
data class QuestionStatementProjection(
	/**
	 * The type discriminator.
	 *
	 * @see ClosedQuestionEntity
	 * @see OpenQuestionEntity
	 */
	val type: Class<out QuestionEntity>,

	/** @see [QuestionEntity.id] */
	val id: Uuid,

	/** @see [QuestionEntity.statement] */
	val statement: RichContent,

	/**
	 * In theory, this is only `null` if `type == OpenQuestionEntity::class.java`.
	 * However, this shouldn't be relied upon for discriminating the types. The source of truth is [type] exclusively.
	 *
	 * Using what's effectively a side-channel opens up the risk of type confusion attacks (CWE-843). After a type
	 * confusion, the software is effectively tainted and anything that happens might be UB.
	 *
	 * @see [ClosedQuestionEntity.choices]
	 */
	val choices: List<FormattedText>?,

	/**
	 * Just like for [choices], in theory only `null` if `type == OpenQuestionEntity::class.java`.
	 *
	 * @see [ClosedQuestionEntity.multiple]
	 */
	val multiple: Boolean?,
)
