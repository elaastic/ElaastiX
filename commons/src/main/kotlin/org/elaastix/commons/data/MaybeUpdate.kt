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

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Wrapper class that materialises both the absence of data and a null value as two distinct concepts.
 * Allows REST PATCH endpoints and the like to distinguish between "keep the current value" and "set null".
 *
 * **WARNING**: When using this in a serializable class, you MUST do the following:
 * - The default value MUST be specified, and MUST be [MaybeUpdate.Keep].
 * - The encoder's `encodeDefaults` MUST be `false` (the default), or [kotlinx.serialization.EncodeDefault] MUST be
 *   applied on the property with [kotlinx.serialization.EncodeDefault.Mode.ALWAYS].
 *
 *  See [MaybeUpdateSerializer] for the rationale behind these requirements.
 */
@Serializable(with = MaybeUpdateSerializer::class)
sealed class MaybeUpdate<out T> {
    /** Represents a no-op state of [MaybeUpdate], i.e. no value was provided. */
    object Keep : MaybeUpdate<Nothing>()

    /** Represents an actual value, eventually nullable, that has been explicitly provided. */
    class Update<T>(
        /** The provided value. */
        val value: T,
    ) : MaybeUpdate<T>()

    override fun equals(other: Any?): Boolean =
        when (other) {
            is Keep -> this is Keep
            is Update<*> -> this is Update && this.value == other.value
            else -> false
        }

    override fun hashCode(): Int =
        when (this) {
            is Keep -> this::class.java.hashCode()

            is Update<*> ->
                when (this.value) {
                    null -> this::class.java.hashCode()
                    else -> 31 * this::class.java.hashCode() + this.value.hashCode()
                }
        }

    override fun toString(): String =
        when (this) {
            is Keep -> "MaybeUpdate.Keep"

            is Update<*> ->
                when (this.value) {
                    null -> "MaybeUpdate.Update(null)"
                    else -> "MaybeUpdate.Update(${this.value})"
                }
        }
}

/**
 * Serializer for [MaybeUpdate]. Expects to never encounter a [MaybeUpdate.Keep] value during serialization.
 *
 * As it is not possible at the type-level nor at the serializer-level to have custom logic for whether a value
 * should be encoded or not, reliance on `encodeDefaults` as documented in [MaybeUpdate] is the only viable strategy.
 */
class MaybeUpdateSerializer<T>(private val valueSerializer: KSerializer<T>) : KSerializer<MaybeUpdate<T>> {
    override val descriptor = SerialDescriptor("org.elaastix.commons.data.MaybeUpdate", valueSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: MaybeUpdate<T>) =
        when (value) {
            is MaybeUpdate.Update -> encoder.encodeSerializableValue(valueSerializer, value.value)

            is MaybeUpdate.Keep ->
                throw SerializationException(
                    "Unexpected value for MaybeUpdate! " +
                        "Is the default value and default encoding strategy properly configured?",
                )
        }

    override fun deserialize(decoder: Decoder): MaybeUpdate<T> =
        // Decoder only calls deserialize if a value is present. If it's missing, it'll default to `Keep` (invariant)
        MaybeUpdate.Update(decoder.decodeSerializableValue(valueSerializer))
}

/**
 * Helper to transform a value of any type into a value wrapped in [MaybeUpdate].
 */
inline fun <reified T> T.asUpdateOp(): MaybeUpdate<T> = MaybeUpdate.Update(this)
