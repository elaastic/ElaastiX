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
import io.swagger.v3.core.converter.AnnotatedType
import io.swagger.v3.core.converter.ModelConverter
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.media.ArbitrarySchema
import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.ComposedSchema
import io.swagger.v3.oas.models.media.JsonSchema
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.media.StringSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.elaastix.commons.data.MaybeUpdate
import org.elaastix.commons.data.Uuid
import org.springdoc.core.customizers.PropertyCustomizer
import org.springframework.boot.kotlinx.serialization.json.autoconfigure.KotlinxSerializationJsonProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import java.lang.reflect.Type
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.superclasses
import kotlin.reflect.jvm.jvmName

/**
 * Autoconfiguration class importing SpringDoc components.
 */
@Configuration
class BuiltinCustomisers(private val kotlinxSerializationJsonProperties: KotlinxSerializationJsonProperties) {
	/**
	 * OpenAPI model converter resolving well-known types such as [Uuid].
	 */
	@Bean
	fun commonTypesConverter(): ModelConverter {
		fun resolve(type: Type): Schema<*>? =
			when (type) {
				Set::class.java -> {
					println("=!!=!!=")
					println(type)
					println("=!!=!!=")
					null
				}

				is CollectionType -> {
					println("===")
					println(type.rawClass)
					println(type.contentType)
					println("===")
					null
				}
				is SimpleType -> resolve(type.rawClass)

				Uuid::class.java ->
					StringSchema().apply {
						pattern = "[a-zA-Z0-9]{25}"
					}

				else -> null
			}

		val stack = mutableListOf<AnnotatedType>()

		return { aType, context, chain ->
			when (val type = aType.type) {
				Set::class.java -> {
					// Jackson doesn't understand Set. So we need to do it the hard way.

					println(context)
					println(aType)
					println(aType.name)
					println(aType.propertyName)
					// println((stack.last().type as SimpleType).rawClass.kotlin)
					// println((stack.last().type as SimpleType).rawClass.kotlin.memberProperties.find { it.name == aType.propertyName })
					// val colType = CollectionType(type)
				}
			}

			stack.add(aType)

			val res = resolve(aType.type) ?:
				if (chain.hasNext()) chain.next().resolve(aType, context, chain)
				else null

			stack.removeLast()

			res
		}
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
	fun valueClassInterop(): DtoCustomiser = { schema, _, _ ->
		schema.apply {
			properties = properties?.mapKeys { (k, _) ->
				val idx = k.indexOf('-')
				if (idx > 0) k.substring(0, idx) else k
			}
		}
	}

	/**
	 * [DtoCustomiser] marking properties as required unless their type is [MaybeUpdate].
	 */
	@Bean
	fun requiredProperties(): DtoCustomiser = { schema, clazz, _ ->
		schema.apply {
			properties?.forEach { (k, _) ->
				if (clazz.memberProperties.find { it.name == k }?.returnType != MaybeUpdate::class)
					addRequiredItem(k)
			}
		}
	}

	/**
	 * [DtoCustomiser] dealing with Kotlinx Serialization closed polymorphism.
	 *
	 * Since these cannot be detected by simple reflection, we need to detect it ourselves.
	 */
	@Bean
	fun kotlinxClosedPolymorphism(): DtoCustomiser = { schema, clazz, _ ->
		val discriminator = kotlinxSerializationJsonProperties.classDiscriminator
		when {
			clazz.isClosedPolymorphicSerdeRoot() -> {
				val possibleTypes = clazz.sealedSubclasses
				 	.filter { !it.isAbstract }
					.map { JsonSchema().apply { `$ref` = Components.COMPONENTS_SCHEMAS_REF + it.jvmName } }

				ArbitrarySchema().apply {
					allOf = listOf(
						schema,
						ArbitrarySchema().apply {
							oneOf = possibleTypes
						}
					)
				}
			}

			clazz.isMemberOfClosedPolymorphicSerde() -> {
				schema.addRequiredItem(discriminator)
				schema.addProperty(
					discriminator,
					StringSchema().apply {
						setConst(clazz.serdeDiscriminator)
					},
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
