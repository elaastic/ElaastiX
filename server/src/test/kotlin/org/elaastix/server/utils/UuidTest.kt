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

package org.elaastix.server.utils

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.TemporalUnitWithinOffset
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

class UuidTest {
    @Test
    fun `generates a proper version 7 UUID`() {
        val uuid = generateUuid7()

        assertThat(uuid.version()).isEqualTo(7)
    }

    @Test
    fun `generates a UUID whose timestamp is very close to now`() {
        val generatedTimestamp = generateUuid7().asInstant()

        assertThat(generatedTimestamp).isCloseTo(
            Instant.now(),
            TemporalUnitWithinOffset(5, ChronoUnit.MILLIS),
        )
    }

    @Test
    fun `retrieves the correct timestamp from a valid UUID`() {
        val uuid = UUID.fromString("01513ef9-b0ef-76d3-acf8-65db5d4fcc56")
        assertThat(uuid.asInstant()).isEqualTo(Instant.ofEpochMilli(1448460529903L))
    }

    @Test
    fun `throws IllegalStateException when trying to get the timestamp of a non-v7 UUID`() {
        assertThrows<IllegalStateException> { UUID.randomUUID().asInstant() }
    }

    @Test
    fun `generated UUIDs are strictly monotonic`() {
        var prev: UUID = generateUuid7()
        repeat(1000) {
            val next = generateUuid7()
            assertThat(prev).isLessThan(next)
            prev = next
        }
    }
}
