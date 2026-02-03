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

import java.security.SecureRandom
import java.time.Instant
import java.util.UUID
import java.util.concurrent.atomic.AtomicReference

// SPDX-SnippetBegin
// SPDX-License-Identifier: Apache-2.0
// SPDX-SnippetCopyrightText: Copyright Red Hat Inc. and Hibernate Authors
// https://github.com/hibernate/hibernate-orm/blob/7b238cf87ad5f041413baf14bead82262a99db13/hibernate-core/src/main/java/org/hibernate/id/uuid/UuidVersion7Strategy.java#L36

private const val UUID7_VERSION_NUMBER = 7
private const val MAX_RANDOM_SEQUENCE = 0x3FFF_FFFF_FFFF_FFFFL

/*
 *	If next random sequence is less or equal to last one sub-millisecond part
 * 	should be incremented to preserve monotonicity of generated UUIDs.
 * 	To do this smallest number of nanoseconds that will always increase
 * 	sub-millisecond part mapped to 12 bits is
 * 		1_000_000 (nanons per milli) / 4096 (12 bits) = 244.14...
 * 	So 245 is used as smallest integer larger than this value.
 */
private const val NANOS_MINIMUM_INCREMENT = 245L

private val randomNumberGenerator: SecureRandom = SecureRandom()

/**
 * Sub-milliseconds part of timestamp (micro- and nanoseconds) mapped to 12 bit integral value.
 * Calculated as nanos / 1000000 * 4096
 */
@Suppress("MagicNumber")
private fun Instant.nanos12b(): Long = ((nano % 1_000_000L) * 0.004096).toLong()

private data class UuidGeneratorState(val lastTimestamp: Instant, val lastSequence: Long) {
    val nanos = lastTimestamp.nanos12b()

    fun millis() = lastTimestamp.toEpochMilli()

    fun getNextState(): UuidGeneratorState {
        val now = Instant.now()
        if (lastTimestampEarlierThan(now)) {
            return UuidGeneratorState(now, randomSequence())
        } else {
            val nextSequence = randomSequence()
            val nextTimestamp =
                if (lastSequence >= nextSequence) {
                    lastTimestamp.plusNanos(NANOS_MINIMUM_INCREMENT)
                } else {
                    lastTimestamp
                }

            return UuidGeneratorState(nextTimestamp, nextSequence)
        }
    }

    private fun lastTimestampEarlierThan(now: Instant) = lastTimestamp.toEpochMilli() < now.toEpochMilli() ||
        (lastTimestamp.toEpochMilli() == now.toEpochMilli() && nanos < now.nanos12b())

    private fun randomSequence() = randomNumberGenerator.nextLong(MAX_RANDOM_SEQUENCE + 1)
}

private val generatorState = AtomicReference(UuidGeneratorState(Instant.EPOCH, Long.MIN_VALUE))

/**
 * Generates a UUID version 7.
 * Uses a cryptographically secure random number generator and guarantees monotonicity.
 */
@Suppress("MagicNumber")
fun generateUuid7(): UUID {
    val state = generatorState.updateAndGet(UuidGeneratorState::getNextState)

    return UUID(
        // MSB bits 0-47 - 48-bit big-endian unsigned number of the Unix Epoch timestamp in milliseconds
        ((state.millis() shl 16) and 0xFFFF_FFFF_FFFF_0000UL.toLong())
            // MSB bits 48-51 - version = 7
            or (UUID7_VERSION_NUMBER.toLong() shl 12)
            // MSB bits 52-63 - sub-milliseconds part of timestamp
            or (state.nanos and 0xFFFL),
        // LSB bits 0-1 - variant = 4
        0x8000_0000_0000_0000UL.toLong()
            // LSB bits 2-63 - pseudorandom counter
            or state.lastSequence,
    )
}

// SPDX-SnippetEnd

/**
 * Converts a UUID version 7 into an Instant.
 * @throws IllegalStateException if the version of the UUID is not 7.
 */
@Suppress("MagicNumber")
fun UUID.asInstant(): Instant {
    if (version() != UUID7_VERSION_NUMBER) {
        error("Only UUIDv7 can be converted into an Instant.")
    }

    val timestamp = (mostSignificantBits ushr 16) and 0xFFFFFFFFFFFFL
    return Instant.ofEpochMilli(timestamp)
}
