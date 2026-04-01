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

    /**
     * A linear grade, as an arbitrary number between zero and an upper bound.
     */
    interface ScalarGrade {
        /** The given grade. MUST be less than or equal to [max]. */
        val grade: UInt

        /** Maximum grade that can be obtained. MUST be non-zero. */
        val max: UInt

        /**
         * Returns the grade as a decimal value between 0 and 1.
         *
         * Suitable for all general purpose use-cases, but not entirely lossless.
         *
         * @see asBigDecimal
         */
        fun asDouble(): Double = grade.toDouble() / max.toDouble()

        /**
         * Returns the grade as a lossless decimal value between 0 and 1.
         *
         * While lossless and suitable for exact arithmetic, it is not appropriate for general purpose use due to
         * the memory and performance overhead (on average 2-3x slower than [Double]).
         *
         * @see asDouble
         */
        fun asBigDecimal(): BigDecimal = BigDecimal.valueOf(grade.toLong()).divide(BigDecimal.valueOf(max.toLong()))
    }
}
