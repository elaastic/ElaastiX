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

package org.elaastix.mm.content.internal

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import org.elaastix.mm.content.RichContent

/**
 * JPA converter responsible for handling the conversion between [RichContent] (and its subclasses) and a plain JSON.
 */
@Converter(autoApply = true)
class ContentJpaConverter : AttributeConverter<RichContent, String> {
    override fun convertToDatabaseColumn(attribute: RichContent?): String? {
        if (attribute == null) return null

        val data = ContentWrapper(
            clazz = attribute::class.java.name,
            data = attribute.toJson(),
        )

        return Json.encodeToString(data)
    }

    override fun convertToEntityAttribute(dbData: String?): RichContent? {
        if (dbData == null) return null
        val wrapper: ContentWrapper = Json.Default.decodeFromString(dbData)
        val clazz = Class.forName(wrapper.clazz).kotlin

        val factoryClazz = clazz.nestedClasses.find { it.isCompanion && it.simpleName == "Factory" }
        checkNotNull(factoryClazz) {
            "Target content class ${clazz.qualifiedName} does not have a Factory companion"
        }

        val factory = factoryClazz.objectInstance as? RichContent.Factory
        checkNotNull(factory) {
            "Target content class ${clazz.qualifiedName}'s Factory does not implement the expected factory interface"
        }

        return factory.fromJson(wrapper.data)
    }

    @Serializable
    private data class ContentWrapper(
        @SerialName("c")
        val clazz: String,
        @SerialName("d")
        val data: JsonElement,
    )
}
