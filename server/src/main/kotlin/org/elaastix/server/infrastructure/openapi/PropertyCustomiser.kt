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

package org.elaastix.server.infrastructure.openapi

import com.fasterxml.jackson.databind.type.SimpleType
import io.swagger.v3.core.converter.AnnotatedType
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.media.StringSchema
import org.elaastix.commons.data.Uuid
import org.springdoc.core.customizers.PropertyCustomizer
import org.springframework.stereotype.Component
import java.lang.reflect.Type

/**
 * OpenAPI property customiser setting the schema of well-known types such as [Uuid].
 */
@Component
class PropertyCustomiser : PropertyCustomizer {
	override fun customize(property: Schema<*>?, aType: AnnotatedType) =
		when (val t = aType.type) {
			is SimpleType -> customisePlainType(property, t.rawClass)
			else -> customisePlainType(property, t)
		}

	private fun customisePlainType(property: Schema<*>?, typ: Type) =
		when (typ) {
			Uuid::class.java ->
				StringSchema().apply {
					pattern = "[a-zA-Z0-9]{25}"
					example = property?.example ?: "2t2razan0q9kzr7gr55oi54j"
					description = property?.description
				}

			else -> property
		}
}
