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

package org.elaastix.server

import org.springframework.http.MediaType

/** Returns whether a given set of media types favours CBOR over JSON. */
fun List<MediaType>.prefersCbor(): Boolean {
	val cbor = indexOf(MediaType.APPLICATION_CBOR)
	val json = indexOf(MediaType.APPLICATION_JSON)

	return when {
		cbor == -1 -> false
		json == -1 -> true
		else -> cbor < json
	}
}
