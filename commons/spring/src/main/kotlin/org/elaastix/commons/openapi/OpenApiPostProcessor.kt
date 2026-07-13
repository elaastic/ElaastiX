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

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.Paths
import io.swagger.v3.oas.models.headers.Header
import io.swagger.v3.oas.models.media.ComposedSchema
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.parameters.Parameter
import io.swagger.v3.oas.models.parameters.RequestBody
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.responses.ApiResponses
import org.elaastix.commons.openapi.utils.copyMetaFromTypeCompatible
import org.elaastix.commons.openapi.utils.hasExtension
import org.elaastix.commons.openapi.utils.isSumOrProductType
import org.elaastix.commons.openapi.utils.resolve
import org.springdoc.core.customizers.OpenApiCustomizer
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Post-processor cleaning up the generated OpenAPI specification.
 * Gets rid of undesired schemas from closed polymorphisms, and collapses empty composites.
 */
@Suppress("TooManyFunctions")
class OpenApiPostProcessor : OpenApiCustomizer {
	companion object {
		/** Marker to apply as an extension, for the post-processor to clean it up. */
		internal const val MARKER_TO_PRUNE = "___PRUNE___"
	}

	override fun customise(openapi: OpenAPI) {
		openapi.paths = openapi.paths?.process(openapi)
		openapi.webhooks = openapi.webhooks?.process(openapi)
		openapi.components = openapi.components?.process(openapi)
	}

	@Suppress("CyclomaticComplexMethod") // Don't worry about it :)
	private fun Schema<*>.process(spec: OpenAPI): Schema<*>? {
		anyOf = anyOf?.process(spec)
		allOf = allOf?.process(spec)
		oneOf = oneOf?.process(spec)

		val schema = when {
			spec.shouldPrune(this) -> null

			anyOf?.size == 0 || allOf?.size == 0 || oneOf?.size == 0 -> null

			anyOf?.size == 1 -> anyOf.first().copyMetaFromTypeCompatible(this@process)

			allOf?.size == 1 -> allOf.first().copyMetaFromTypeCompatible(this@process)

			oneOf?.size == 1 -> oneOf.first().copyMetaFromTypeCompatible(this@process)

			else -> also {
				properties = properties?.process(spec)
				items = items?.process(spec)
				`if` = `if`?.process(spec)
				then = then?.process(spec)
				`else` = `else`?.process(spec)
			}
		}

		// While this would be best practice, `openapi-typescript` struggles with it.
		//
		// val discriminator = json.configuration.classDiscriminator
		// if (!schema?.oneOf.isNullOrEmpty() && spec.resolveProperties(schema).contains(discriminator)) {
		// 	schema.discriminator = Discriminator().apply { propertyName = discriminator }
		// }

		// Fix: SpringDoc botches nullable types for sum types and product types
		if (schema?.types?.contains("null") == true && schema.isSumOrProductType()) {
			schema.types = null
			val nullSchema = Schema<Any>().apply { addType("null") }
			return when (val of = oneOf) {
				null -> ComposedSchema().apply {
					oneOf = mutableListOf(
						nullSchema,
						schema,
					)
				}

				else -> also { of.add(nullSchema) }
			}
		}

		return schema
	}

	@OptIn(ExperimentalContracts::class)
	private fun OpenAPI.shouldPrune(schema: Schema<*>?): Boolean {
		contract { returns(false) implies (schema != null) }

		return when {
			schema?.hasExtension(MARKER_TO_PRUNE) != false -> true
			schema.`$ref` != null -> shouldPrune(resolve(schema))
			else -> false
		}
	}

	//
	// .process hell
	//

	@JvmName("process1")
	private fun Paths.process(spec: OpenAPI) = transformTo(Paths()) { it.process(spec) }

	@JvmName("process2")
	private fun Map<String, PathItem>.process(spec: OpenAPI) = transform { it.process(spec) }

	@JvmName("process3")
	private fun PathItem.process(spec: OpenAPI) =
		let {
			get = get?.process(spec)
			put = put?.process(spec)
			head = head?.process(spec)
			post = post?.process(spec)
			delete = delete?.process(spec)
			patch = patch?.process(spec)
			options = options?.process(spec)
			trace = trace?.process(spec)

			if (isEmpty()) null else this
		}

	@JvmName("process4")
	private fun Operation.process(spec: OpenAPI) =
		also { _ ->
			// Little polish on the description and summary.
			// Gets rid of the period on the summary, and removes the duplicate summary text from the description.
			summary = summary?.trimEnd('.')
			description = description?.let {
				when (val idx = it.indexOf("\n\n")) {
					-1 -> null
					else -> it.substring(idx).trim().ifEmpty { null }
				}
			}

			parameters = parameters?.process(spec)
			requestBody = requestBody?.process(spec)
			responses = responses?.process(spec)
		}

	@JvmName("process5")
	private fun Map<String, Parameter>.process(spec: OpenAPI) = transform { it.process(spec) }

	@JvmName("process6")
	private fun List<Parameter>.process(spec: OpenAPI) = mapNotNull { it.process(spec) }.ifEmpty { null }

	@JvmName("process7")
	private fun Parameter.process(spec: OpenAPI) =
		also {
			schema = schema?.process(spec)
			content = content?.process(spec)
		}

	@JvmName("process8")
	private fun Map<String, RequestBody>.process(spec: OpenAPI) = transform { it.process(spec) }

	@JvmName("process9")
	private fun RequestBody.process(spec: OpenAPI) =
		also {
			content = content?.process(spec)
		}

	@JvmName("process10")
	private fun ApiResponses.process(spec: OpenAPI) = transformTo(ApiResponses()) { it.process(spec) }

	@JvmName("process11")
	private fun Map<String, ApiResponse>.process(spec: OpenAPI) = transform { it.process(spec) }

	@JvmName("process12")
	private fun ApiResponse.process(spec: OpenAPI) =
		also {
			content = content?.process(spec)
		}

	@JvmName("process13")
	private fun Content.process(spec: OpenAPI) = transformTo(Content()) { it.process(spec) }

	@JvmName("process14")
	private fun MediaType.process(spec: OpenAPI) =
		also {
			schema = schema?.process(spec)
		}

	@JvmName("process15")
	private fun Components.process(spec: OpenAPI) =
		also {
			schemas = schemas?.process(spec)
			responses = responses?.process(spec)
			parameters = parameters?.process(spec)
			requestBodies = requestBodies?.process(spec)
			headers = headers?.process(spec)
		}

	@JvmName("process16")
	private fun Map<String, Header>.process(spec: OpenAPI) = transform { it.process(spec) }

	@JvmName("process17")
	private fun Header.process(spec: OpenAPI) =
		also {
			content = content?.process(spec)
			schema = schema?.process(spec)
		}

	@JvmName("process18")
	private fun Map<String, Schema<*>>.process(spec: OpenAPI) = transform { it.process(spec) }

	@JvmName("process19")
	private fun List<Schema<*>>.process(spec: OpenAPI) = mapNotNull { it.process(spec) }

	//
	// helpers
	//

	private fun PathItem.isEmpty() =
		get == null &&
			put == null &&
			head == null &&
			post == null &&
			delete == null &&
			patch == null &&
			options == null &&
			trace == null

	private inline fun <K, V, R> Map<K, V>.transform(block: (V) -> R): Map<K, R>? = transformTo(mutableMapOf(), block)

	private inline fun <K, V, R, H : MutableMap<K, R>> Map<K, V>.transformTo(holder: H, block: (V) -> R): H? =
		holder.also {
			forEach { (k, v) ->
				val res = block(v)
				if (res != null) holder[k] = res
			}
		}.ifEmpty { null }
}
