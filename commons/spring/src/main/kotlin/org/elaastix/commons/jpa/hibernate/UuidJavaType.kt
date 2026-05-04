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

package org.elaastix.commons.jpa.hibernate

import org.elaastix.commons.data.Uuid
import org.hibernate.dialect.Dialect
import org.hibernate.type.descriptor.WrapperOptions
import org.hibernate.type.descriptor.java.AbstractClassJavaType
import org.hibernate.type.descriptor.java.JavaType
import org.hibernate.type.descriptor.java.UUIDJavaType
import org.hibernate.type.descriptor.jdbc.JdbcType
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators
import java.util.UUID
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

/**
 * Hibernate [JavaType] to convert to and from Kotlin-native [Uuid].
 */
object UuidJavaType : AbstractClassJavaType<Uuid>(Uuid::class.java) {
	override fun isInstance(value: Any) = value is Uuid

	override fun cast(value: Any?) = value as Uuid?

	override fun useObjectEqualsHashCode() = true

	override fun getRecommendedJdbcType(context: JdbcTypeIndicators): JdbcType =
		UUIDJavaType.INSTANCE.getRecommendedJdbcType(context)

	override fun getDefaultSqlLength(dialect: Dialect, jdbcType: JdbcType): Long =
		UUIDJavaType.INSTANCE.getDefaultSqlLength(dialect, jdbcType)

	override fun toString(value: Uuid): String = value.toHexDashString()

	override fun fromString(string: CharSequence): Uuid = Uuid.parse(string.toString())

	override fun <X> unwrap(value: Uuid?, type: Class<X>, options: WrapperOptions): X? =
		value?.let {
			when {
				UUID::class.java.isAssignableFrom(type) -> type.cast(it.toJavaUuid())
				String::class.java.isAssignableFrom(type) -> type.cast(it.toHexDashString())
				ByteArray::class.java.isAssignableFrom(type) -> type.cast(it.toByteArray())
				else -> throw unknownUnwrap(type)
			}
		}

	override fun <X> wrap(value: X, options: WrapperOptions): Uuid? =
		when (value) {
			null -> null
			is Uuid -> value
			is UUID -> value.toKotlinUuid()
			is String -> Uuid.parse(value)
			is ByteArray -> Uuid.fromByteArray(value)
			else -> throw unknownWrap(value::class.java)
		}

	@Suppress("unused") // https://jetbrains.com/help/inspectopedia/JavaIoSerializableObjectMustHaveReadResolve.html
	private fun readResolve(): Any = UuidJavaType
}
