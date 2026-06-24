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

package org.elaastix.commons

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.require
import kotlin.time.DurationUnit
import kotlin.time.Instant
import kotlin.time.toDuration

/** Casts a type to another type. */
inline fun <reified T> Any.cast(): T = T::class.java.cast(this as T)

/** Same as [let], with a type cast. */
@OptIn(ExperimentalContracts::class)
inline fun <reified T, R> Any.letAs(block: (T) -> R): R {
	contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
	return block(this.cast())
}

/** Same as [also], with a type cast. */
@OptIn(ExperimentalContracts::class)
inline fun <reified T> Any.alsoAs(block: (T) -> Unit): T {
	contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
	return cast<T>().also(block)
}

/** Same as [apply], with a type cast. */
@OptIn(ExperimentalContracts::class)
inline fun <reified T> Any.applyAs(block: T.() -> Unit): T {
	contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
	return cast<T>().apply(block)
}

// SPDX-SnippetBegin
// SPDX-SnippetCopyrightText: 2010-2018 JetBrains s.r.o. and Kotlin Programming Language contributors.
// SPDX-License-Identifier: Apache-2.0

/** Same as [map] but returns a set instead of a list. */
inline fun <T, R> Iterable<T>.mapSet(transform: (T) -> R): Set<R> {
	@Suppress("MagicNumber") // From Kotlin source code
	return mapTo(LinkedHashSet(if (this is Collection<*>) this.size else 10), transform)
}

// SPDX-SnippetEnd

/**
 * Converts this instant to the ISO 8601 string representation, truncating sub-second information.
 *
 * @see Instant.toString
 */
fun Instant.toIsoStringSecondPrecise() = minus(nanosecondsOfSecond.toDuration(DurationUnit.NANOSECONDS)).toString()

/**
 * Truncates the string to be of a given maximum length, if needed.
 *
 * If the string length is less than or equal to the maximum, it is returned as-is.
 * Otherwise, the first `maximum - 2` characters are returned plus an ellipsis.
 *
 * The rationale for cutting off 2 letters is to ensure byte-length compatibility, rather than a more
 * sophisticated Unicode grapheme clustering aware length.
 */
@Suppress("MagicNumber")
fun String.truncate(max: UInt): String {
	require(max >= 3u) { "Cannot truncate a string to less than 3 characters." }
	return when {
		this.length <= max.toInt() -> this
		else -> "${this.substring(0, max.minus(2u).toInt())}…"
	}
}

/**
 * Returns the string representation of the double with a fixed number of decimals.
 * The value is rounded down towards zero if needed.
 */
fun Double.toFixed(decimalPlaces: UInt): String =
	"${toLong()}${(this % 1).toString().padEnd(1 + decimalPlaces.toInt(), '0').substring(1, 1 + decimalPlaces.toInt())}"
