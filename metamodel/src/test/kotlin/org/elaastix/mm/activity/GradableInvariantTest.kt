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

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class GradableInvariantTest {
    @Test
    fun `does not allow scalar grades with a negative maximum value`() {
        assertThrows<IllegalArgumentException> { ScalarGrade(grade = 0.0, max = 0.0) }
    }

    @Test
    fun `does not allow scalar grades with a zero maximum value`() {
        assertThrows<IllegalArgumentException> { ScalarGrade(grade = 0.0, max = 0.0) }
    }

    @Test
    fun `does now allow negative scalar grades`() {
        assertThrows<IllegalArgumentException> { ScalarGrade(grade = -10.0, max = 10.0) }
    }

    @Test
    fun `does allow scalar grades equal to zero`() {
        assertDoesNotThrow { ScalarGrade(grade = 0.0, max = 10.0) }
    }

    @Test
    fun `does allow scalar grades equal to the maximum value`() {
        assertDoesNotThrow { ScalarGrade(grade = 10.0, max = 10.0) }
    }

    @Test
    fun `does now allow scalar grades greater than the maximum value`() {
        assertThrows<IllegalArgumentException> { ScalarGrade(grade = 20.0, max = 10.0) }
    }
}
