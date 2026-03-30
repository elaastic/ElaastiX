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
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.cbor.CborDecoder
import kotlinx.serialization.cbor.CborEncoder
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigInteger
import kotlin.uuid.Uuid as KtUuid

private const val BASE36_RADIX = 36

typealias Uuid =
    @Serializable(with = UuidSerializer::class)
    KtUuid

/**
 * Serializer for [KtUuid] values.
 * Encodes UUIDs as a Base36-encoded string, or directly as raw bytes in binary formats (currently only CBOR).
 */
@OptIn(ExperimentalSerializationApi::class) // Anything non-json is experimental atm
object UuidSerializer : KSerializer<KtUuid> {
    @OptIn(InternalSerializationApi::class) // We don't really have a choice anyway
    override val descriptor: SerialDescriptor =
        buildSerialDescriptor("org.elaastix.commons.data.Uuid", SerialKind.CONTEXTUAL)

    override fun serialize(encoder: Encoder, value: KtUuid) = when {
        encoder is CborEncoder -> serializeAsBytes(encoder, value)
        else -> serializeAsString(encoder, value)
    }

    override fun deserialize(decoder: Decoder) = when {
        decoder is CborDecoder -> deserializeFromBytes(decoder)
        else -> deserializeFromString(decoder)
    }

    private fun serializeAsBytes(encoder: Encoder, value: KtUuid) = encoder.encodeSerializableValue(
        ByteArraySerializer(),
        value.toByteArray(),
    )

    private fun deserializeFromBytes(decoder: Decoder) = KtUuid.fromByteArray(
        decoder.decodeSerializableValue(
            ByteArraySerializer(),
        ),
    )

    private fun serializeAsString(encoder: Encoder, value: KtUuid) = encoder.encodeString(
        BigInteger(value.toByteArray()).toString(BASE36_RADIX),
    )

    private fun deserializeFromString(decoder: Decoder) = KtUuid.fromByteArray(
        BigInteger(decoder.decodeString(), BASE36_RADIX).toByteArray(),
    )
}
