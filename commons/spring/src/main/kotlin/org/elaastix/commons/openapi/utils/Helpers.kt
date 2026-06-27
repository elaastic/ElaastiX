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

import io.swagger.v3.core.converter.AnnotatedType
import io.swagger.v3.core.converter.ModelConverter
import io.swagger.v3.core.converter.ModelConverterContext
import io.swagger.v3.oas.models.media.Schema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.superclasses
import kotlin.reflect.jvm.jvmName

/**
 * Whether the type is [Serializable].
 */
internal fun KClass<*>.isSerializable(): Boolean = hasAnnotation<Serializable>()

/**
 * Checks is a type is a [Serializable] sealed interface.
 */
internal fun KClass<*>.isPhantomMemberOfClosedPolymorphicSerde(): Boolean =
	isSealed && constructors.isEmpty() && isSerializable()

/**
 * Checks if the type is a [Serializable] member of a sealed hierarchy of [Serializable].
 */
internal fun KClass<*>.isMemberOfClosedPolymorphicSerde(): Boolean =
	superclasses.any { (isSealed && isSerializable()) || it.isMemberOfClosedPolymorphicSerde() }

/**
 * Value of the discriminator field if the type is part of a sealed hierarchy of [Serializable].
 */
internal val KClass<*>.serdeDiscriminator: String
	get() = findAnnotations<SerialName>().firstOrNull()?.value ?: jvmName

/**
 * Resolves the schema using the next resolver in the chain. Returns `null` if there is no resolver left to try.
 */
internal fun Iterator<ModelConverter>.resolveNext(type: AnnotatedType, context: ModelConverterContext) =
	when {
		hasNext() -> next().resolve(type, context, this)
		else -> null
	}

/**
 * Helper adding a property to the schema, also marking it as required.
 */
internal fun Schema<*>.addRequiredProperty(key: String, property: Schema<*>): Schema<*> =
	if (properties?.containsKey(key) != true) addProperty(key, property).addRequiredItem(key) else this
