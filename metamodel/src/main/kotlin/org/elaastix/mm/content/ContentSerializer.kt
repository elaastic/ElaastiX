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

package org.elaastix.mm.content

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement
import kotlin.reflect.jvm.jvmName

/**
 * Kotlinx serializer for all types of content. Currently only compatible with JSON.
 *
 * TODO: Add support for CBOR serialization
 */
abstract class AbstractContentSerializer<T : RichContent> internal constructor() : KSerializer<T> {
    internal val delegate = ContentWrapper.serializer()

    // TODO: Detect public vs private serde contexts, use SerializableContent.identifier for public use.
    override fun serialize(encoder: Encoder, value: T) =
        encoder.encodeSerializableValue(
            delegate,
            ContentWrapper(
                clazz = value::class.jvmName,
                data = value.toJson(),
            ),
        )

    override fun deserialize(decoder: Decoder): T {
        val wrapper: ContentWrapper = decoder.decodeSerializableValue(delegate)
        val clazz = Class.forName(wrapper.clazz).kotlin

        val factoryClazz = clazz.nestedClasses.find { it.isCompanion && it.simpleName == "Factory" }
        checkNotNull(factoryClazz) {
            "Target content class ${clazz.qualifiedName} does not have a Factory companion"
        }

        val factory = factoryClazz.objectInstance as? RichContent.Factory
        checkNotNull(factory) {
            "Target content class ${clazz.qualifiedName}'s Factory does not implement the expected factory interface"
        }

        @Suppress("UNCHECKED_CAST")
        return factory.fromJson(wrapper.data) as T
    }

    @Serializable
    internal data class ContentWrapper(
        @SerialName("c")
        val clazz: String,
        @SerialName("d")
        val data: JsonElement,
    )
}

/** Serializer for [RichContent]. */
class RichContentSerializer : AbstractContentSerializer<RichContent>() {
    override val descriptor = SerialDescriptor("org.elaastix.mm.RichContent", delegate.descriptor)
}

/** Serializer for [FormattedContent]. */
class FormattedContentSerializer : AbstractContentSerializer<FormattedContent>() {
    override val descriptor = SerialDescriptor("org.elaastix.mm.FormattedContent", delegate.descriptor)
}

/** Serializer for [RichContent]. */
class FormattedTextSerializer : AbstractContentSerializer<FormattedText>() {
    override val descriptor = SerialDescriptor("org.elaastix.mm.FormattedText", delegate.descriptor)
}
