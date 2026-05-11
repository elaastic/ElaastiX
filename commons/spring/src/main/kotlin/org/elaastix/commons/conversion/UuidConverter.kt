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

package org.elaastix.commons.conversion

import org.elaastix.commons.data.Uuid
import org.elaastix.commons.data.UuidSerializer.fromBase36
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

/**
 * Converter to let Spring handle Base36-encoded Uuids.
 */
@Component
class UuidConverter : Converter<String, Uuid> {
	override fun convert(source: String): Uuid = Uuid.fromBase36(source)
}
