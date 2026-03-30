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

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MaybeUpdateTest {
    companion object {
        @Serializable
        data class TestObj(
            val prop1: String?,
            val prop2: MaybeUpdate<String> = MaybeUpdate.Keep,
            val prop3: MaybeUpdate<String?> = MaybeUpdate.Keep,
        )

        @Serializable
        data class BadTestObj(val prop: MaybeUpdate<Int>)

        val OBJ_1 = TestObj(prop1 = "meow")
        const val JSON_1 = """{"prop1":"meow"}"""

        val OBJ_2 = TestObj(
            prop1 = "meow",
            prop2 = "meow".asUpdateOp(),
            prop3 = null.asUpdateOp(),
        )
        const val JSON_2 = """{"prop1":"meow","prop2":"meow","prop3":null}"""

        val OBJ_3 = TestObj(
            prop1 = "meow",
            prop2 = "meow".asUpdateOp(),
        )
        const val JSON_3 = """{"prop1":"meow","prop2":"meow"}"""

        val OBJ_4 = BadTestObj(MaybeUpdate.Update(16))
        const val JSON_4 = """{"prop":16}"""

        val BAD_OBJ_1 = BadTestObj(MaybeUpdate.Keep)
        const val BAD_JSON_1 = """{"prop1":"meow","prop2":null}"""
    }

    @Nested
    inner class Serialization {
        private inline fun <reified T> test(obj: T, json: String) {
            assertThat(Json.encodeToString<T>(obj)).isEqualTo(json)
        }

        @Test
        fun `serializes objects with only 'normal' props specified`() = test(OBJ_1, JSON_1)

        @Test
        fun `serializes objects with all props specified`() = test(OBJ_2, JSON_2)

        @Test
        fun `serializes objects with a single optional props specified`() = test(OBJ_3, JSON_3)

        @Test
        fun `serializes an object violating MaybeUpdate default value invariant if a value is set`() =
            test(OBJ_4, JSON_4)

        @Test
        fun `fails to serialize an object violating MaybeUpdate default value invariant`() {
            assertThrows<SerializationException> { Json.encodeToString(BAD_OBJ_1) }
        }
    }

    @Nested
    inner class Deserialization {
        private inline fun <reified T> test(json: String, obj: T) {
            assertThat(Json.decodeFromString<T>(json)).isEqualTo(obj)
        }

        @Test
        fun `deserialises objects with only 'normal' props specified`() = test(JSON_1, OBJ_1)

        @Test
        fun `deserialises objects with all props specified`() = test(JSON_2, OBJ_2)

        @Test
        fun `deserialises objects with a single optional props specified`() = test(JSON_3, OBJ_3)

        @Test
        fun `rejects null values set in non-nullable optional values`() {
            assertThrows<SerializationException> { Json.decodeFromString<TestObj>(BAD_JSON_1) }
        }
    }
}
