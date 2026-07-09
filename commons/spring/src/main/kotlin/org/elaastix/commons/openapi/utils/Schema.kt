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

@file:Suppress("TooManyFunctions")

package org.elaastix.commons.openapi.utils

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.media.Schema
import org.elaastix.commons.openapi.OpenApiPostProcessor

internal typealias SchemaMap = MutableMap<String, Schema<*>>

/**
 * Helper checking the presence of an extension.
 */
internal fun Schema<*>.hasExtension(ext: String): Boolean = extensions?.containsKey(ext) == true

/**
 * Helper checking if the schema is a sum type or a product type.
 */
internal fun Schema<*>.isSumOrProductType(): Boolean = (oneOf?.size ?: anyOf?.size ?: allOf?.size ?: 0) > 0

/**
 * Copy the metadata of [other] into the schema.
 */
internal fun Schema<*>.copyMetaFrom(other: Schema<*>) =
	apply {
		title = other.title
		description = other.description
		readOnly = other.readOnly
		writeOnly = other.writeOnly
		externalDocs = other.externalDocs
		deprecated = other.deprecated
	}

/**
 * Copy the metadata of [other] into the schema.
 */
internal fun <T> Schema<in T>.copyMetaFromTypeCompatible(other: Schema<out T>) =
	apply {
		copyMetaFrom(other)
		required = other.required
		example = other.example
		enum = other.enum
		multipleOf = other.multipleOf
		maximum = other.maximum
		minimum = other.minimum
		maxLength = other.maxLength
		minLength = other.minLength
		pattern = other.pattern
		maxItems = other.maxItems
		minItems = other.minItems
		uniqueItems = other.uniqueItems
		maxProperties = other.maxProperties
		minProperties = other.minProperties
	}

/**
 * Mark as to-be-pruned by [OpenApiPostProcessor].
 */
internal fun Schema<*>.markToPrune() = addExtension(OpenApiPostProcessor.MARKER_TO_PRUNE, true)

internal fun String.trimJsonSchemaRef() = substring(Components.COMPONENTS_SCHEMAS_REF.length)
