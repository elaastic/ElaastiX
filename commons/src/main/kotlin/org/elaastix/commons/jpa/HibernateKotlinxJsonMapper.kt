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

package org.elaastix.commons.jpa

import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.hibernate.type.descriptor.WrapperOptions
import org.hibernate.type.descriptor.java.JavaType
import org.hibernate.type.format.FormatMapper

/**
 * Alternative [FormatMapper] for Hibernate designed to work with Kotlinx.serialization.
 * Replaces the default one that relies on Jackson being present on the classpath.
 */
@Suppress("unused") // Used by hibernate.properties
class HibernateKotlinxJsonMapper : FormatMapper {
    override fun <T> fromString(charSequence: CharSequence, javaType: JavaType<T>, wrapperOptions: WrapperOptions): T {
        val serializer = serializer(javaType.javaType)
        @Suppress("UNCHECKED_CAST") // SAFETY: Kotlinx must've picked a suitable deserializer.
        return Json.decodeFromString(serializer, charSequence.toString()) as T
    }

    override fun <T> toString(value: T, javaType: JavaType<T>, wrapperOptions: WrapperOptions): String? {
        if (value == null) return null
        val serializer = serializer(javaType.javaType)
        return Json.encodeToString(serializer, value)
    }
}
