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

import com.fasterxml.jackson.databind.type.CollectionType
import com.fasterxml.jackson.databind.type.SimpleType
import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.media.StringSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.elaastix.commons.data.Uuid
import org.springdoc.core.customizers.PropertyCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.lang.reflect.Type
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.superclasses
import kotlin.reflect.jvm.jvmName

/**
 * Autoconfiguration class importing SpringDoc components.
 */
@Configuration(proxyBeanMethods = false)
class BuiltinCustomisers(private val json: Json) {
	/**
	 * OpenAPI property customiser setting the schema of well-known types such as [Uuid].
	 */
	@Bean
	fun commonTypesCustomiser(): PropertyCustomizer {
		fun customiser(schema: Schema<*>?, baseType: Type): Schema<*>? {
			val type =
				when (baseType) {
					is CollectionType ->
						return (schema ?: ArraySchema()).apply { items = customiser(items, baseType.contentType) }

					is SimpleType -> baseType.rawClass

					else -> baseType
				}

			return when (type) {
				Uuid::class.java ->
					StringSchema().apply {
						pattern = "[a-zA-Z0-9]{25}"
						example = schema?.example ?: "02t2razan0q9kzr7gr55oi54j"
						description = schema?.description
					}

				// We should not send `(U)Long` over the wire because JavaScript does not deal with them properly.
				// ULong::class.java ->

				// Would be cool but isn't applied consistently because of Kotlin inlining shenanigans.
				// We might need to use smth akin to the DtoCustomiser, but this is minor and will have to wait :)
				// UInt::class.java ->
				//     IntegerSchema().apply {
				//         format = "uint32"
				//     }

				else -> schema
			}
		}

		return { schema, type -> customiser(schema, type.type) }
	}

	/**
	 * [DtoCustomiser] dealing with Kotlin value class interop.
	 *
	 * Kotlin generates synthetic getters with a random suffix for value classes, which appear in the documentation.
	 * Since Kotlin separates the name and the suffix with a dash and this character cannot be present elsewhere, the
	 * converter detects these properties and strip the prefix.
	 *
	 * See the relevant [documentation](https://kotlinlang.org/docs/inline-classes.html#mangling).
	 */
	@Bean
	fun valueClassInterop(): DtoCustomiser = { schema, _ ->
		schema.apply {
			properties = properties?.mapKeys { (k, _) ->
				val idx = k.indexOf('-')
				if (idx > 0) k.substring(0, idx) else k
			}
		}
	}

	/**
	 * [DtoCustomiser] dealing with Kotlinx Serialization closed polymorphism.
	 *
	 * Since these cannot be detected by simple reflection, we need to detect it ourselves.
	 */
	@Bean
	fun kotlinxClosedPolymorphism(): DtoCustomiser = { schema, clazz ->
		when {
			clazz.isClosedPolymorphicSerdeRoot() -> {
				val possibleTypes = clazz.sealedSubclasses
					.filter { !it.isAbstract }
					.map { it.serdeDiscriminator }

				schema.addProperty(
					json.configuration.classDiscriminator,
					StringSchema().apply {
						enum = possibleTypes
						description = "Discriminator field of the union type"
					},
				)
			}

			clazz.isMemberOfClosedPolymorphicSerde() -> {
				schema.addProperty(
					json.configuration.classDiscriminator,
					StringSchema().apply { setConst(clazz.serdeDiscriminator) },
				)
			}

			else -> schema
		}
	}

	private fun KClass<*>.isClosedPolymorphicSerdeRoot(): Boolean = isSealed && hasAnnotation<Serializable>()

	private fun KClass<*>.isMemberOfClosedPolymorphicSerde(): Boolean =
		superclasses.any { isClosedPolymorphicSerdeRoot() || it.isMemberOfClosedPolymorphicSerde() }

	private val KClass<*>.serdeDiscriminator: String
		get() = findAnnotations<SerialName>().firstOrNull()?.value ?: jvmName
}
