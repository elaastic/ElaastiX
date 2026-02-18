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

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.hibernate.boot.model.TypeContributions
import org.hibernate.boot.model.TypeContributor
import org.hibernate.service.ServiceRegistry
import java.time.ZoneOffset
import java.util.UUID
import kotlin.time.Instant
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid
import java.time.OffsetDateTime as JavaOffsetDateTime

class HibernateTypeContributor : TypeContributor {
    companion object {
        // Workaround for https://hibernate.atlassian.net/browse/HHH-20070
        private var initialised = false
    }

    override fun contribute(typeContributions: TypeContributions, serviceRegistry: ServiceRegistry) {
        if (!initialised) {
            initialised = true
            typeContributions.contributeAttributeConverter(UuidConverter::class.java)
            typeContributions.contributeAttributeConverter(InstantConverter::class.java)
        }
    }
}

@Converter(autoApply = true)
class UuidConverter : AttributeConverter<Uuid, UUID> {
    override fun convertToDatabaseColumn(attribute: Uuid?): UUID? = attribute?.toJavaUuid()

    override fun convertToEntityAttribute(dbData: UUID?): Uuid? = dbData?.toKotlinUuid()
}

@Converter(autoApply = true)
class InstantConverter : AttributeConverter<Instant, JavaOffsetDateTime> {
    override fun convertToDatabaseColumn(attribute: Instant?): JavaOffsetDateTime? =
        attribute?.toJavaInstant()?.atOffset(ZoneOffset.UTC)

    override fun convertToEntityAttribute(dbData: JavaOffsetDateTime?): Instant? =
        dbData?.toInstant()?.toKotlinInstant()
}
