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

@file:ExcludeFromCoverage("Converters are doing trivial conversions that are barely more than no-op")

package org.elaastix.commons.jpa

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.elaastix.commons.data.Uuid
import org.elaastix.commons.jpa.hibernate.UuidJavaType
import org.elaastix.commons.platform.ExcludeFromCoverage
import org.hibernate.boot.model.TypeContributions
import org.hibernate.boot.model.TypeContributor
import org.hibernate.service.ServiceRegistry
import java.util.UUID
import kotlin.time.Instant
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid
import java.time.Instant as JavaInstant

/**
 * Custom [TypeContributor] responsible for registering the package's converters to the Hibernate context.
 *
 * Implementation has to cope with Hibernate bug [HHH-20070](https://hibernate.atlassian.net/browse/HHH-20070);
 * the contributor is invoked twice, causing duplicate registration that leads to application start failure.
 *
 * The contributor will be automatically picked by Hibernate thanks to the
 * `META-INF/services/org.hibernate.boot.model.TypeContributor` file.
 */
class HibernateTypeContributor : TypeContributor {
    // Workaround for https://hibernate.atlassian.net/browse/HHH-20070
    private var initialised = false

    override fun contribute(typeContributions: TypeContributions, serviceRegistry: ServiceRegistry) {
        if (!initialised) {
            initialised = true
            typeContributions.contributeAttributeConverter(UuidConverter::class.java)
            typeContributions.contributeAttributeConverter(InstantConverter::class.java)
            typeContributions.contributeAttributeConverter(UIntConverter::class.java)
            typeContributions.contributeAttributeConverter(ULongConverter::class.java)
            typeContributions.contributeJavaType(UuidJavaType)
        }
    }
}

/**
 * JPA Converter responsible for handling the conversion from [Uuid] to [UUID].
 * Automatically applied, to allow seamless usage of [Uuid] in entities.
 *
 * [UUID] is a [basic type](https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#a486) that
 * JPA can deal with.
 */
@Converter(autoApply = true)
class UuidConverter : AttributeConverter<Uuid, UUID> {
    override fun convertToDatabaseColumn(attribute: Uuid?): UUID? = attribute?.toJavaUuid()

    override fun convertToEntityAttribute(dbData: UUID?): Uuid? = dbData?.toKotlinUuid()
}

/**
 * JPA Converter responsible for handling the conversion from Kotlin's [Instant] to [JavaInstant].
 * Automatically applied, to allow seamless usage of [Instant] in entities.
 *
 * Recent versions of JPA (3.2) classify [JavaInstant] as a
 * [basic type](https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#a486), so no further
 * conversion is required.
 */
@Converter(autoApply = true)
class InstantConverter : AttributeConverter<Instant, JavaInstant> {
    override fun convertToDatabaseColumn(attribute: Instant?): JavaInstant? = attribute?.toJavaInstant()

    override fun convertToEntityAttribute(dbData: JavaInstant?): Instant? = dbData?.toKotlinInstant()
}

/**
 * JPA Converter responsible for handling the conversion from [UInt] to [Int].
 * Automatically applied, to allow seamless usage of [UInt] in entities.
 */
@Converter(autoApply = true)
@ExcludeFromCoverage("Trivially equivalent to a no-op")
class UIntConverter : AttributeConverter<UInt, Int> {
    override fun convertToDatabaseColumn(attribute: UInt?): Int? = attribute?.toInt()

    override fun convertToEntityAttribute(dbData: Int?): UInt? = dbData?.toUInt()
}

/**
 * JPA Converter responsible for handling the conversion from [ULong] to [Long].
 * Automatically applied, to allow seamless usage of [ULong] in entities.
 */
@Converter(autoApply = true)
@ExcludeFromCoverage("Trivially equivalent to a no-op")
class ULongConverter : AttributeConverter<ULong, Long> {
    override fun convertToDatabaseColumn(attribute: ULong?): Long? = attribute?.toLong()

    override fun convertToEntityAttribute(dbData: Long?): ULong? = dbData?.toULong()
}
