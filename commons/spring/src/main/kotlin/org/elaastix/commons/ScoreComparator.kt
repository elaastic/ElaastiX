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

/**
 * Generic comparator that uses a scoring function.
 * The scoring function is not limited to numerical values and may return anything that implements [Comparable].
 *
 * Entries with a large score will be placed before entries with a low score. This is the opposite of the classical
 * sorting order, where entries are sorted from lowest to highest. For scoring from lowest to highest, use [reversed].
 *
 * @param T The type of objects that can be sorted.
 * @param S The score returned by the scoring function.
 * @property scorer The scoring function to use. The function should be pure!
 */
class ScoreComparator<T, S : Comparable<S>>(val scorer: Scorer<T, S>) : Comparator<T> {
	override fun compare(a: T, b: T): Int {
		val sa = scorer.score(a)
		val sb = scorer.score(b)
		return sb.compareTo(sa)
	}
}

/**
 * Functional interface describing an object that can give a score to objects of type [T].
 * The score may be anything that is [Comparable].
 *
 * @param T The type of objects this object can score.
 * @param S The type of score this object returns.
 */
fun interface Scorer<T, S : Comparable<S>> {
	/**
	 * The scoring function.
	 */
	fun score(value: T): S

	/**
	 * Wraps the scorer into a full-fledged [Comparator] via [ScoreComparator].
	 */
	fun toComparator(): ScoreComparator<T, S> = ScoreComparator(this)
}

/** Returns a list of all elements sorted according to the specified scoring function via [ScoreComparator]. */
fun <T, S : Comparable<S>> Iterable<T>.sortedWith(scorer: Scorer<T, S>) = sortedWith(scorer.toComparator())

/** Converts the collection to a sorted set using a scoring function via [ScoreComparator]. */
fun <T, S : Comparable<S>> Iterable<T>.toSortedSet(scorer: Scorer<T, S>) = toSortedSet(scorer.toComparator())
