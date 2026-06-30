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

import io.swagger.v3.oas.models.media.StringSchema
import kotlinx.serialization.json.Json
import org.elaastix.commons.data.MaybeUpdate
import org.elaastix.commons.data.Uuid
import org.elaastix.commons.openapi.utils.addRequiredProperty
import org.elaastix.commons.openapi.utils.copyMetaFrom
import org.elaastix.commons.openapi.utils.isMemberOfClosedPolymorphicSerde
import org.elaastix.commons.openapi.utils.isPhantomMemberOfClosedPolymorphicSerde
import org.elaastix.commons.openapi.utils.markToPrune
import org.elaastix.commons.openapi.utils.serdeDiscriminator
import org.springframework.context.annotation.Bean
import kotlin.reflect.full.memberProperties

/**
 * Autoconfiguration class importing SpringDoc components.
 */
class BuiltinCustomisers(private val json: Json) {
	/**
	 * OpenAPI property customiser setting the schema of well-known types such as [Uuid].
	 */
	@Bean
	fun commonTypesCustomiser(): TypeCustomiser =
		{ schema, type, _, _, _ ->
			when (type) {
				Uuid::class -> UuidSchema().copyMetaFrom(schema)

				// Note: Sending ULong is risky; JS can only represent them safely up to 53 bits.
				ULong::class -> schema.apply { format = "uint64" }

				UInt::class -> schema.apply { format = "uint32" }

				else -> schema
			}
		}

	/**
	 * [SerializableCustomiser] dealing with Kotlin value class interop.
	 *
	 * Kotlin generates synthetic getters with a random suffix for value classes, which appear in the documentation.
	 * Since Kotlin separates the name and the suffix with a dash and this character cannot be present elsewhere, the
	 * converter detects these properties and strip the prefix.
	 *
	 * See the relevant [documentation](https://kotlinlang.org/docs/inline-classes.html#mangling).
	 */
	@Bean
	fun valueClassInterop(): SerializableCustomiser =
		{ schema, _, _, _, _ ->
			schema.apply {
				properties = properties?.mapKeys { (k, _) ->
					val idx = k.indexOf('-')
					if (idx > 0) k.substring(0, idx) else k
				}
			}
		}

	/**
	 * [SerializableCustomiser] marking properties as required unless their type is [MaybeUpdate].
	 *
	 * Complements [org.springdoc.core.customizers.KotlinNullablePropertyCustomizer], which does not update the
	 * `required` property of the schemas.
	 */
	@Bean
	@Suppress("NestedBlockDepth")
	fun requiredProperties(): SerializableCustomiser =
		{ schema, clazz, _, _, _ ->
			schema.apply {
				(allOf?.mapNotNull { it.properties?.keys }?.flatten() ?: properties?.keys)?.let { props ->
					for (property in clazz.memberProperties) {
						if (
							property.returnType != MaybeUpdate::class &&
							props.contains(property.name) &&
							required?.contains(property.name) != true
						) {
							addRequiredItem(property.name)
						}
					}
				}
			}
		}

	/**
	 * [SerializableCustomiser] dealing with Kotlinx Serialization closed polymorphism.
	 *
	 * Since these cannot be detected by simple reflection, we need to detect it ourselves.
	 */
	@Bean
	fun kotlinxClosedPolymorphism(): SerializableCustomiser =
		{ schema, clazz, _, _, _ ->
			when {
				clazz.isPhantomMemberOfClosedPolymorphicSerde() -> {
					schema.markToPrune()
					schema
				}

				clazz.isMemberOfClosedPolymorphicSerde() -> {
					schema.addRequiredProperty(
						json.configuration.classDiscriminator,
						StringSchema().apply {
							description = "Discriminator property. Must be present to identify the type among discriminated unions."
							setConst(clazz.serdeDiscriminator)
						},
					)
				}

				else -> schema
			}
		}
}
