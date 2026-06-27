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
import com.fasterxml.jackson.databind.type.MapType
import com.fasterxml.jackson.databind.type.SimpleType
import io.swagger.v3.core.converter.AnnotatedType
import io.swagger.v3.core.converter.ModelConverter
import io.swagger.v3.core.converter.ModelConverterContext
import io.swagger.v3.oas.models.media.Schema
import org.elaastix.commons.data.MaybeUpdate
import org.elaastix.commons.openapi.utils.resolveNext
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order

/**
 * Swagger [ModelConverter] unwrapping [MaybeUpdate] values so the resolved schema is the inner type.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
class MaybeUpdateConverter : ModelConverter {
	override fun resolve(
		aType: AnnotatedType,
		context: ModelConverterContext,
		chain: Iterator<ModelConverter>,
	): Schema<*>? {
		when (val type = aType.type) {
			is CollectionType if type.contentType.rawClass == MaybeUpdate::class.java -> {
				type.contentType.bindings.getBoundType(0)?.let {
					aType.type = CollectionType.construct(
						type.rawClass,
						type.bindings,
						type.superClass,
						type.interfaces.toTypedArray(),
						it,
					)
				}
			}

			is MapType if type.contentType.rawClass == MaybeUpdate::class.java -> {
				type.contentType.bindings.getBoundType(0)?.let {
					aType.type = MapType.construct(
						type.rawClass,
						type.bindings,
						type.superClass,
						type.interfaces.toTypedArray(),
						type.keyType,
						it,
					)
				}
			}

			is SimpleType if type.rawClass == MaybeUpdate::class.java -> {
				type.bindings.getBoundType(0)?.let { inner ->
					aType.type = inner
				}
			}
		}

		return chain.resolveNext(aType, context)
	}
}
