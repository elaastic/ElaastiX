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

import jakarta.persistence.Entity
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import org.elaastix.mm.activity.Material
import org.elaastix.mm.content.RichContent
import org.elaastix.server.core.AbstractEntityWithAuthorship
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

/**
 * Generic question. Lacks important properties that are defined by subclasses.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
abstract class QuestionEntity(
	/** The question's statement. */
	@JdbcTypeCode(SqlTypes.JSON)
	@Suppress("JpaAttributeTypeInspection") // https://youtrack.jetbrains.com/issue/IDEA-191568
	var statement: RichContent,

	/** The explanation of the expected answer. Optional. */
	@JdbcTypeCode(SqlTypes.JSON)
	@Suppress("JpaAttributeTypeInspection") // https://youtrack.jetbrains.com/issue/IDEA-191568
	var answerExplanation: RichContent?,
) : AbstractEntityWithAuthorship(),
	Material
