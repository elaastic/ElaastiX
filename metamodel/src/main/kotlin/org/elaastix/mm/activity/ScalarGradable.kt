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

import java.math.BigDecimal

/**
 * Denotes objects that can be given a grade, on a linear scale between zero and an arbitrary upper bound.
 * Such grades can be represented as-is (e.g. 16/20), or as a percentage.
 * Usually implemented by classes representing a production of a learner.
 *
 * @see ScalarGrade
 */
interface ScalarGradable {
    /** The scalar grade given to the object. */
    val absoluteGrade: ScalarGrade

    /** A linear grade, as an arbitrary number between zero and an upper bound. */
    data class ScalarGrade(
        /** Maximum grade that can be obtained. MUST be non-zero and positive. */
        val max: BigDecimal,
        /** The given grade. MUST conform to `0 <= grade <= max`. */
        val grade: BigDecimal,
    ) {
        init {
            require(grade.signum() != -1) { "Grade must be positive or zero" }
            require(max.signum() == 1) { "Maximum value of the grade must be strictly positive" }
            require(grade.compareTo(max) != 1) { "Grade must be less than or equal to the maximum grade" }
        }
    }
}
