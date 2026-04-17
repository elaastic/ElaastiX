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

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.json.Json
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class UuidSerializerTest {
    companion object {
        @Serializable
        data class TestObj(val value: Uuid)

        val TEST_UUID = Uuid.parseHexDash(TEST_UUID_V7)

        const val TEST_UUID_V7 = "01513edc-8c60-7bb2-9f74-ebc6ee214673"
        const val TEST_UUID_B36 = "2t2razan0q9kzr7gr55oi54j"
        val TEST_UUID_BYTES =
            @OptIn(ExperimentalUnsignedTypes::class)
            ubyteArrayOf(
                // Using unsigned bytes because signed bytes (the default...) is a pita
                0x01u, 0x51u, 0x3eu, 0xdcu, 0x8cu, 0x60u, 0x7bu, 0xb2u,
                0x9fu, 0x74u, 0xebu, 0xc6u, 0xeeu, 0x21u, 0x46u, 0x73u,
            ).asByteArray()

        val TEST_OBJ = TestObj(TEST_UUID)

        val CBOR_ENCODED =
            @Suppress("Indentation") // On purpose, for readability.
            @OptIn(ExperimentalUnsignedTypes::class)
            ubyteArrayOf(
                // @formatter:off
                (0xA0 or 1).toUByte(), // map(1)
                (0x60 or 5).toUByte(), // text(5)
                0x76u, 0x61u, 0x6Cu, 0x75u, 0x65u, // "value"
                (0x40 or 16).toUByte(), // bytes(16)
                *TEST_UUID_BYTES.asUByteArray(), // ...
                // @formatter:on
            ).asByteArray()

        const val JSON_ENCODED = """{"value":"$TEST_UUID_B36"}"""
    }

    @Nested
    inner class JsonUuidTest {
        @Test
        fun `serialises Uuid as a Base36 string in JSON format`() {
            val json = Json.encodeToString(TEST_OBJ)
            assertThat(json).isEqualTo(JSON_ENCODED)
        }

        @Test
        fun `deserialises Uuid from a Base36 string in JSON format`() {
            val obj: TestObj = Json.decodeFromString(JSON_ENCODED)
            assertThat(obj).isEqualTo(TEST_OBJ)
        }
    }

    @Nested
    @OptIn(ExperimentalSerializationApi::class)
    inner class CborUuidTest {
        // Gotcha: https://github.com/Kotlin/kotlinx.serialization/issues/3058
        // We (will; cbor not yet configured at all) autoconfigure things in Spring for more idiomatic CBOR serde ootb
        @Suppress("PropertyName", "VariableNaming") // To shadow `Cbor`
        val Cbor = Cbor {
            alwaysUseByteString = true
            useDefiniteLengthEncoding = true
        }

        @Test
        fun `serialises Uuid as a Base36 string in CBOR format`() {
            val cbor = Cbor.encodeToByteArray(TEST_OBJ)
            assertThat(cbor).isEqualTo(CBOR_ENCODED)
        }

        @Test
        fun `deserialises Uuid from a Base36 string in CBOR format`() {
            val obj: TestObj = Cbor.decodeFromByteArray(CBOR_ENCODED)
            assertThat(obj).isEqualTo(TEST_OBJ)
        }
    }
}
