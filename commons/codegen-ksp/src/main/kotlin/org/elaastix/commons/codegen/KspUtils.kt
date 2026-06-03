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

package org.elaastix.commons.codegen

import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import kotlin.reflect.KClass

/**
 * Clone of [getAnnotationsByType] that does not wrap the [KSAnnotation] in a proxy.
 * Needed to pull [KClass] argument values, see KSP-888.
 */
fun KSAnnotated.getKSAnnotationsByType(clazz: KClass<out Annotation>) =
	// SPDX-SnippetBegin
	// SPDX-SnippetCopyrightText: 2020 Google LLC
	// SPDX-SnippetCopyrightText: 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
	// SPDX-License-Identifier: Apache-2.0
		annotations.filter {
			it.shortName.getShortName() == clazz.simpleName && it.annotationType.resolve().declaration
				.qualifiedName?.asString() == clazz.qualifiedName
		}
	// SPDX-SnippetEnd

/**
 * Checks if the annotated element has any of the specified annotations.
 */
fun KSAnnotated.hasAnyAnnotation(vararg classes: KClass<out Annotation>) =
	// SPDX-SnippetBegin
	// SPDX-SnippetCopyrightText: 2020 Google LLC
	// SPDX-SnippetCopyrightText: 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
	// SPDX-License-Identifier: Apache-2.0
	annotations.any {
		classes.any { clazz ->
			it.shortName.getShortName() == clazz.simpleName && it.annotationType.resolve().declaration
				.qualifiedName?.asString() == clazz.qualifiedName
		}
	}
	// SPDX-SnippetEnd

/**
 * Helper to call [Resolver.getSymbolsWithAnnotation] with a [KClass] reference instead of the FQN.
 */
fun Resolver.getSymbolsWithAnnotation(annotation: KClass<out Annotation>, inDepth: Boolean = false) =
	getSymbolsWithAnnotation(annotation.java.name, inDepth)

/**
 * Shortcut to avoid the common dance with getting and filtering code elements.
 */
fun Resolver.getFilteredSymbolsWithAnnotation(annotation: KClass<out Annotation>, inDepth: Boolean = false) =
	getSymbolsWithAnnotation(annotation, inDepth)
		.filter { it.validate() }
		.filterIsInstance<KSClassDeclaration>()
