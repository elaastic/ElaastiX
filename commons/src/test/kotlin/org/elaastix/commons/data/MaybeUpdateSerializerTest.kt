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

class MaybeUpdateSerializerTest {
    companion object {
        @Serializable
        data class TestObj(
            val prop1: String?,
            val prop2: MaybeUpdate<String> = MaybeUpdate.Keep,
            val prop3: MaybeUpdate<String?> = MaybeUpdate.Keep,
        )

        @Serializable
        data class BadTestObj(val prop: MaybeUpdate<Int>)
    }

    @Nested
    inner class Serialization {
        private inline infix fun <reified T> T.serializesTo(json: String) {
            assertThat(Json.encodeToString<T>(this)).isEqualTo(json)
        }

        @Test
        fun `serializes objects with only 'normal' props specified`() =
            TestObj(prop1 = "meow") serializesTo """{"prop1":"meow"}"""

        @Test
        fun `serializes objects with all props specified`() =
            TestObj(prop1 = "meow", prop2 = "meow".asUpdateOp(), prop3 = null.asUpdateOp()) serializesTo
                """{"prop1":"meow","prop2":"meow","prop3":null}"""

        @Test
        fun `serializes objects with a single optional props specified`() =
            TestObj(prop1 = "meow", prop2 = "meow".asUpdateOp()) serializesTo
                """{"prop1":"meow","prop2":"meow"}"""

        @Test
        fun `serializes an object violating MaybeUpdate default value invariant if a value is set`() =
            BadTestObj(MaybeUpdate.Update(16)) serializesTo
                """{"prop":16}"""

        @Test
        fun `fails to serialize an object violating MaybeUpdate default value invariant`() {
            assertThrows<SerializationException> { Json.encodeToString(BadTestObj(MaybeUpdate.Keep)) }
        }
    }

    @Nested
    inner class Deserialization {
        private inline infix fun <reified T> String.deserializesTo(obj: T) {
            assertThat(Json.decodeFromString<T>(this)).isEqualTo(obj)
        }

        @Test
        fun `deserialises objects with only 'normal' props specified`() =
            """{"prop1":"meow"}""" deserializesTo TestObj(prop1 = "meow")

        @Test
        fun `deserialises objects with all props specified`() =
            """{"prop1":"meow","prop2":"meow","prop3":null}""" deserializesTo
                TestObj(
                    prop1 = "meow",
                    prop2 = "meow".asUpdateOp(),
                    prop3 = null.asUpdateOp(),
                )

        @Test
        fun `deserialises objects with a single optional props specified`() =
            """{"prop1":"meow","prop2":"meow"}""" deserializesTo
                TestObj(
                    prop1 = "meow",
                    prop2 = "meow".asUpdateOp(),
                )

        @Test
        fun `rejects null values set in non-nullable optional values`() {
            assertThrows<SerializationException> {
                Json.decodeFromString<TestObj>("""{"prop1":"meow","prop2":null}""")
            }
        }
    }
}
