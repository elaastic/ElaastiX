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

package org.elaastix.commons.data

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.elaastix.commons.utils.requireJsonString
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class RequiresJsonStringTest {
	@Test
	fun `passes when given a JSON string`() {
		assertDoesNotThrow { requireJsonString(JsonPrimitive("meow")) }
	}

	@Test
	fun `throws when given a non-string primitive JSON element`() {
		assertThrows<IllegalArgumentException> { requireJsonString(JsonPrimitive(44)) }
	}

	@Test
	fun `throws when given a non-primitive JSON element`() {
		assertThrows<IllegalArgumentException> { requireJsonString(JsonObject(emptyMap())) }
	}
}
