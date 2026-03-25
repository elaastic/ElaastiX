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

/**
 * Denotes objects that can be given a grade on an absolute scale.
 * Usually implemented by classes representing a production of a learner.
 *
 * @see AbsoluteGrade
 */
interface AbsoluteGradable {
    /** The absolute grade given to the object. */
    val absoluteGrade: AbsoluteGrade

    /** Grades supported by absolute notation. */
    enum class AbsoluteGrade {
        /** Failing grade (e.g. wrong answer on a question). If only partially failing, consider using [PARTIAL] */
        FAILED,

        /** Passing grade (e.g. correct answer on a question). If only partially passing, consider using [PARTIAL] */
        PASS,

        /** Partially passing grade (e.g. one correct and one incorrect choices for a MCQ). */
        PARTIAL,

        /** Virtual grade used in situations where grading is not the intent (e.g. using a question as a poll). */
        NEUTRAL,
    }
}
