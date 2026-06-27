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

package org.elaastix.commons.openapi.utils

import io.swagger.v3.core.converter.ModelConverterContext
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.media.Schema

/**
 * Resolves a schema, walking the `$ref` property.
 */
internal fun OpenAPI.resolve(schema: Schema<*>, recurse: Boolean = false) = components.schemas.resolve(schema, recurse)

/**
 * Resolves a schema, walking the `$ref` property.
 */
internal fun ModelConverterContext.resolve(schema: Schema<*>, recurse: Boolean = false) =
	definedModels.resolve(schema, recurse)

/**
 * Resolves a schema, walking the `$ref` property.
 */
internal tailrec fun SchemaMap.resolve(schema: Schema<*>, recurse: Boolean): Schema<*>? =
	when (val ref = schema.`$ref`) {
		null -> schema

		else -> {
			when (val resolved = this[ref.trimJsonSchemaRef()]) {
				null -> null
				else -> if (recurse) resolve(resolved, true) else resolved
			}
		}
	}

/**
 * Resolves the properties that the schema is guaranteed to have.
 */
internal fun OpenAPI.resolveProperties(schema: Schema<*>): Set<String> = components.schemas.resolveProperties(schema)

/**
 * Resolves the properties that the schema is guaranteed to have.
 */
internal fun ModelConverterContext.resolveProperties(schema: Schema<*>): Set<String> =
	definedModels.resolveProperties(schema)

/**
 * Resolves the properties that the schema is guaranteed to have.
 */
private fun SchemaMap.resolveProperties(schema: Schema<*>): Set<String> =
	when {
		schema.oneOf != null ->
			schema.oneOf.map { resolveProperties(it) }.reduceOrNull { acc, props -> acc.intersect(props) }

		schema.anyOf != null ->
			schema.anyOf.map { resolveProperties(it) }.reduceOrNull { acc, props -> acc.intersect(props) }

		schema.allOf != null ->
			schema.allOf.map { resolveProperties(it) }.reduceOrNull { acc, props -> acc.union(props) }

		schema.`$ref` != null ->
			resolve(schema, true)?.let { resolveProperties(it) }

		schema.properties != null ->
			schema.properties.keys

		else -> null
	}.orEmpty()

/**
 * Replaces a schema by another. Walks the refs of the original schema and only replaces the concrete schema.
 */
internal fun Components.replace(schema: Schema<*>, target: Schema<*>) = schemas.replace(schema, target)

/**
 * Replaces a schema by another. Walks the refs of the original schema and only replaces the concrete schema.
 */
internal tailrec fun MutableMap<String, Schema<*>>.replace(schema: Schema<*>, target: Schema<*>) {
	if (schema === target) return
	val key = schema.`$ref`?.trimJsonSchemaRef() ?: return

	val ref = this[key]
	when (ref?.`$ref`) {
		null -> this[key] = target
		else -> replace(ref, target)
	}
}
