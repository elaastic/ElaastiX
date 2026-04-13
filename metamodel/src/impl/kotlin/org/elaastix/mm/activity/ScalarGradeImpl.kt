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

package org.elaastix.mm.activity

import jakarta.persistence.Embeddable

@JvmRecord
@Embeddable
@PublishedApi
@Suppress(
    "RedundantModalityModifier", // all-open wants to make it open; explicit final is needed to get a Java Record.
    "JpaEntityWithValAttributesInspection", // https://youtrack.jetbrains.com/issue/KTIJ-37754
)
internal final data class ScalarGradeImpl(
    // Validation using Jakarta is too annoying (cross-field validation boilerplate is HUGE).
    // Validated in the form of a smart constructor instead. Much less verbose, will still do the trick :)
    override val grade: Double,
    override val max: Double,
) : ScalarGradable.ScalarGrade

/**
 * Factory to create an instance of [ScalarGradable.ScalarGrade].
 */
fun ScalarGrade(grade: Double, max: Double): ScalarGradable.ScalarGrade {
    require(grade >= 0) { "Grade must be positive or zero" }
    require(max > 0) { "Maximum value of the grade must be strictly positive" }
    require(grade <= max) { "Grade must be less than or equal to the maximum grade" }

    return ScalarGradeImpl(grade, max)
}
