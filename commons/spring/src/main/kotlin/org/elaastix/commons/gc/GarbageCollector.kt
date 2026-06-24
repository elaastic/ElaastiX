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

package org.elaastix.commons.gc

/**
 * Interface for beans that implement a garbage collection mechanism.
 * Beans that implement this interface will have their [gc] method invoked periodically.
 *
 * It primarily serves as a defence mechanism against runaway memory usage due to holding references to objects that
 * should no longer be available.
 */
interface GarbageCollector {
	/**
	 * Procedure periodically called that should perform a full scrub over the held data and free any resource that
	 * is no longer necessary.
	 */
	fun gc()
}
