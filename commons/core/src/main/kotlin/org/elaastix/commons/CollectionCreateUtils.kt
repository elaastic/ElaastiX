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

@file:Suppress("TooManyFunctions", "Unused")

package org.elaastix.commons

/**
 * Creates a mutable list of size [size], populating it by calling [factory] for each value.
 */
inline fun <T> makeList(size: Int, factory: (Int) -> T): MutableList<T> =
	mutableListOf<T>().apply { repeat(size) { add(factory(it)) } }

/**
 * Creates a mutable list of size [size], populating it by calling [factory] for each value.
 */
inline fun <T> makeList(size: UInt, factory: (UInt) -> T): MutableList<T> =
	mutableListOf<T>().apply { repeat(size.toInt()) { add(factory(it.toUInt())) } }

/**
 * Creates a mutable list of size [size], populating it by calling [factory] for each value.
 * Variant that doesn't pass the index of current iteration to the factory.
 * Named differently to avoid issues with overload resolution. KT-18451
 */
inline fun <T> makeListN(size: Int, factory: () -> T): MutableList<T> =
	mutableListOf<T>().apply { repeat(size) { add(factory()) } }

/**
 * Creates a mutable list of size [size], populating it by calling [factory] for each value.
 * Variant that doesn't pass the index of current iteration to the factory.
 * Named differently to avoid issues with overload resolution. KT-18451
 */
inline fun <T> makeListN(size: UInt, factory: () -> T): MutableList<T> =
	mutableListOf<T>().apply { repeat(size.toInt()) { add(factory()) } }

/**
 * Creates a mutable set of size [size], populating it by calling [factory] for each value.
 * The resulting set will not be of the specified size if there are duplicate values returned by the factory.
 */
inline fun <T> makeSet(size: Int, factory: (Int) -> T): MutableSet<T> =
	mutableSetOf<T>().apply { repeat(size) { add(factory(it)) } }

/**
 * Creates a mutable set of size [size], populating it by calling [factory] for each value.
 * The resulting set will not be of the specified size if there are duplicate values returned by the factory.
 */
inline fun <T> makeSet(size: UInt, factory: (Int) -> T): MutableSet<T> =
	mutableSetOf<T>().apply { repeat(size.toInt()) { add(factory(it)) } }

/**
 * Creates a mutable set of size [size], populating it by calling [factory] for each value.
 * The resulting set will not be of the specified size if there are duplicate values returned by the factory.
 * Variant that doesn't pass the index of current iteration to the factory.
 * Named differently to avoid issues with overload resolution. KT-18451
 */
inline fun <T> makeSetN(size: Int, factory: () -> T): MutableSet<T> =
	mutableSetOf<T>().apply { repeat(size) { add(factory()) } }

/**
 * Creates a mutable set of size [size], populating it by calling [factory] for each value.
 * The resulting set will not be of the specified size if there are duplicate values returned by the factory.
 * Variant that doesn't pass the index of current iteration to the factory.
 * Named differently to avoid issues with overload resolution. KT-18451
 */
inline fun <T> makeSetN(size: UInt, factory: () -> T): MutableSet<T> =
	mutableSetOf<T>().apply { repeat(size.toInt()) { add(factory()) } }

/**
 * Creates a mutable map of size [size], populating it by calling [factory] for each value.
 * The resulting map will not be of the specified size if there are duplicate keys returned by the factory.
 */
inline fun <T, U> makeMap(size: Int, factory: (Int) -> Pair<T, U>): MutableMap<T, U> =
	mutableMapOf<T, U>().apply { repeat(size) { factory(it).let { (k, v) -> put(k, v) } } }

/**
 * Creates a mutable map of size [size], populating it by calling [factory] for each value.
 * The resulting map will not be of the specified size if there are duplicate keys returned by the factory.
 */
inline fun <T, U> makeMap(size: UInt, factory: (Int) -> Pair<T, U>): MutableMap<T, U> =
	mutableMapOf<T, U>().apply { repeat(size.toInt()) { factory(it).let { (k, v) -> put(k, v) } } }

/**
 * Creates a mutable map of size [size], populating it by calling [factory] for each value.
 * The resulting map will not be of the specified size if there are duplicate keys returned by the factory.
 * Variant that doesn't pass the index of current iteration to the factory.
 * Named differently to avoid issues with overload resolution. KT-18451
 */
inline fun <T, U> makeMapN(size: Int, factory: () -> Pair<T, U>): MutableMap<T, U> =
	mutableMapOf<T, U>().apply { repeat(size) { factory().let { (k, v) -> put(k, v) } } }

/**
 * Creates a mutable map of size [size], populating it by calling [factory] for each value.
 * The resulting map will not be of the specified size if there are duplicate keys returned by the factory.
 * Variant that doesn't pass the index of current iteration to the factory.
 * Named differently to avoid issues with overload resolution. KT-18451
 */
inline fun <T, U> makeMapN(size: UInt, factory: () -> Pair<T, U>): MutableMap<T, U> =
	mutableMapOf<T, U>().apply { repeat(size.toInt()) { factory().let { (k, v) -> put(k, v) } } }
