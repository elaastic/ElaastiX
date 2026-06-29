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

package org.elaastix.commons.openapi

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.type.CollectionType
import com.fasterxml.jackson.databind.type.MapType
import com.fasterxml.jackson.databind.type.SimpleType
import io.swagger.v3.core.converter.AnnotatedType
import io.swagger.v3.core.converter.ModelConverter
import io.swagger.v3.core.converter.ModelConverterContext
import io.swagger.v3.oas.models.media.Schema
import org.elaastix.commons.openapi.utils.resolve
import org.elaastix.commons.openapi.utils.resolveNext
import org.elaastix.commons.openapi.utils.trimJsonSchemaRef
import org.springdoc.core.providers.ObjectMapperProvider
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import java.lang.reflect.Type

/**
 * Swagger [ModelConverter] applying all registered [SerializableCustomiser] beans.
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
class TypeCustomisingConverter(
	private val customiserList: List<TypeCustomiser>,
	private val springDocObjectMapperProvider: ObjectMapperProvider,
) : ModelConverter {
	override fun resolve(type: AnnotatedType, context: ModelConverterContext, chain: Iterator<ModelConverter>) =
		chain.resolveNext(type, context)?.let { schema ->
			when (val ref = schema.`$ref`) {
				null -> transform(schema, type.type, type, context)

				else -> schema.also {
					context.resolve(schema, true)?.let { resolved ->
						if (resolved != schema) {
							val name = ref.trimJsonSchemaRef()
							context.defineModel(name, transform(resolved, type.type, type, context), type, name)
						}
					}
				}
			}
		}

	private fun transform(schema: Schema<*>, type: Type, at: AnnotatedType, ctx: ModelConverterContext): Schema<*> =
		when (type) {
			is CollectionType -> {
				schema.items = schema.items?.let { applyTransform(it, type.contentType, at, ctx) }
				applyTransform(schema, type, at, ctx)
			}

			is MapType -> {
				schema.apply {
					val additional = additionalProperties
					if (additional is Schema<*>) {
						additionalProperties = applyTransform(additional, type.contentType, at, ctx)
					}
				}
				applyTransform(schema, type, at, ctx)
			}

			is SimpleType -> {
				applyTransform(schema, type, at, ctx)
			}

			else -> applyTransform(schema, springDocObjectMapperProvider.jsonMapper().constructType(type), at, ctx)
		}

	private fun applyTransform(
		schema: Schema<*>,
		type: JavaType,
		at: AnnotatedType,
		ctx: ModelConverterContext,
	): Schema<*> {
		val klass = type.rawClass.kotlin
		val bindings = type.bindings
		var transformed = schema
		for (customiser in customiserList) {
			transformed = customiser.customise(transformed, klass, bindings, at, ctx)
		}

		return transformed
	}
}
