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

package org.elaastix.commons.boot.autoconfigure

import com.fasterxml.jackson.databind.type.SimpleType
import io.swagger.v3.core.converter.ModelConverter
import io.swagger.v3.oas.models.Components
import kotlinx.serialization.Serializable
import org.elaastix.commons.openapi.BuiltinCustomisers
import org.elaastix.commons.openapi.DtoCustomiser
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import kotlin.reflect.full.hasAnnotation

/**
 * Autoconfiguration class importing SpringDoc components.
 */
@AutoConfiguration
@Import(BuiltinCustomisers::class)
class SpringdocAutoConfiguration(private val dtoCustomiserList: List<DtoCustomiser>) {
	/**
	 * Swagger [io.swagger.v3.core.converter.ModelConverter] applying all registered [DtoCustomiser] beans.
	 *
	 * Kotlin generates synthetic getters with a random suffix for value classes, which appear in the documentation.
	 * Since Kotlin separates the name and the suffix with a dash and this character cannot be present elsewhere, the
	 * converter detects these properties and strip the prefix.
	 *
	 * Implementation based on [org.springdoc.core.customizers.KotlinNullablePropertyCustomizer].
	 */
	@Bean
	fun dtoCustomiserConverter(): ModelConverter {
		return c@{ type, context, chain ->
			if (!chain.hasNext()) return@c null
			chain.next().resolve(type, context, chain)?.also {
				val clazz =
					when (val type = type.type) {
						is SimpleType -> type.rawClass.kotlin
						is Class<*> -> type.kotlin
						else -> return@also
					}

				if (!clazz.hasAnnotation<Serializable>()) return@also

				var schema = it.`$ref`
					?.let { ref -> context.definedModels[ref.substring(Components.COMPONENTS_SCHEMAS_REF.length)] }
					?: it

				for (customiser in dtoCustomiserList) {
					schema = customiser.customise(schema, clazz)
				}
			}
		}
	}
}
