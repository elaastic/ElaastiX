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

package org.elaastix.mm.user.cohort

import org.elaastix.mm.MmObject
import org.elaastix.mm.user.User

/**
 * An organisational-level group of users who share a common discriminator.
 * For example, "2nd-year Computer Science undergraduates" (of a given University).
 *
 * As for [User], any concrete cohort MUST NOT directly inherit from this class, but rather an existing subclass.
 * This interface is effectively sealed, but Kotlin's `sealed` semantics are too restrictive to apply the modifier.
 */
interface Cohort<T : User> : MmObject {
    /**
     * Name of the cohort.
     */
    val name: String

    /**
     * Members of the cohort.
     */
    val members: Set<T>

    /**
     * Administrators of the cohort.
     * They MAY also appear in [members], but otherwise they're not counted as members of the cohort.
     *
     * Unlike for members, there is no requirement regarding the homogeneity of roles. People of different roles can
     * be administrator of the same cohort.
     */
    val administrators: Set<User>
}
