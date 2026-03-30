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

@file:Suppress("ForbiddenImport") // We don't have access to Elaastix Commons here

package org.elaastix.mm

import kotlin.time.Instant
import kotlin.uuid.Uuid

/**
 * Base interface of all objects from the metamodel.
 */
interface MmObject {
    /**
     * A globally unique identifier tied to the object.
     */
    val id: Uuid

    /**
     * Instant at which the creation of the object occurred.
     */
    val createdAt: Instant

    /**
     * Instant at which the last modification occurred.
     * If the object has never been modified, then it is equivalent (but not necessarily equal) to the creation date.
     */
    val updatedAt: Instant
}
