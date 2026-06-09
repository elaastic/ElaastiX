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

import org.elaastix.commons.data.Uuid
import org.elaastix.commons.jpa.entity.AbstractEntity
import org.elaastix.commons.jpa.repository.ElaastixRepository

/** Maps a set of [Uuid] to a set of entities. */
fun <T : AbstractEntity> Set<Uuid>.toRefSet(repo: ElaastixRepository<T>): MutableSet<T> {
	val set = LinkedHashSet<T>(this.size)
	for (id in this) set.add(repo.getReferenceById(id))
	return set
}

/** Maps a list of [Uuid] to a list of entities. */
fun <T : AbstractEntity> List<Uuid>.toRefList(repo: ElaastixRepository<T>): MutableList<T> {
	val list = ArrayList<T>(this.size)
	for (id in this) list.add(repo.getReferenceById(id))
	return list
}

/** Maps a set of [Uuid] to a set of typed entities. */
fun <U : T, T : AbstractEntity> Set<Uuid>.toTypedRefSet(cls: Class<U>, repo: ElaastixRepository<T>): MutableSet<U> {
	val set = LinkedHashSet<U>(this.size)
	for (id in this) set.add(repo.getTypedReferenceById(cls, id))
	return set
}

/** Maps a list of [Uuid] to a list of typed entities. */
fun <U : T, T : AbstractEntity> List<Uuid>.toTypedRefList(cls: Class<U>, repo: ElaastixRepository<T>): MutableList<U> {
	val list = ArrayList<U>(this.size)
	for (id in this) list.add(repo.getTypedReferenceById(cls, id))
	return list
}

/** Reified variant of [toTypedRefSet]. */
inline fun <reified U : T, T : AbstractEntity> Set<Uuid>.toTypedRefSet(repo: ElaastixRepository<T>): MutableSet<U> =
	toTypedRefSet(U::class.java, repo)

/** Reified variant of [toTypedRefList]. */
inline fun <reified U : T, T : AbstractEntity> List<Uuid>.toTypedRefList(repo: ElaastixRepository<T>): MutableList<U> =
	toTypedRefList(U::class.java, repo)

/** Maps a set of [AbstractEntity] to a set of [Uuid]. */
fun Set<AbstractEntity>.toUuidSet(): Set<Uuid> {
	val set = LinkedHashSet<Uuid>(this.size)
	for (entity in this) set.add(entity.id)
	return set
}

/** Maps a list of [AbstractEntity] to a list of [Uuid]. */
fun List<AbstractEntity>.toUuidList(): List<Uuid> {
	val list = ArrayList<Uuid>(this.size)
	for (entity in this) list.add(entity.id)
	return list
}
