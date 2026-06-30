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

package org.elaastix.commons.openapi

import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.media.StringSchema
import org.elaastix.commons.data.Uuid

/**
 * Specialised form of [StringSchema] for UUIDs serialised in Base36.
 */
class UuidSchema : Schema<Uuid>("string", null) {
	companion object {
		/** Regex pattern of UUIDs in Base36. */
		const val PATTERN = "[a-zA-Z0-9]{25}"
	}

	override fun getPattern() = PATTERN
	override fun getFormat() = null

	override fun setPattern(pattern: String?) = throw UnsupportedOperationException()
	override fun pattern(pattern: String?) = throw UnsupportedOperationException()

	override fun setFormat(pattern: String?) = throw UnsupportedOperationException()
	override fun format(pattern: String?) = throw UnsupportedOperationException()
}
