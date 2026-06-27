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

import com.fasterxml.jackson.databind.type.TypeBindings
import io.swagger.v3.core.converter.AnnotatedType
import io.swagger.v3.core.converter.ModelConverterContext
import io.swagger.v3.oas.models.media.Schema
import kotlin.reflect.KClass

/**
 * Interface for customising schemas associated with a given type.
 * All implementors registered as beans will be automatically invoked during schema generation.
 */
@FunctionalInterface
fun interface TypeCustomiser {
	/** The customisation logic. Returns the transformed schema (or [schema]). */
	fun customise(
		schema: Schema<*>,
		clazz: KClass<*>,
		bindings: TypeBindings,
		annotatedType: AnnotatedType,
		context: ModelConverterContext,
	): Schema<*>
}
