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

package org.elaastix.commons.spring

import org.springframework.core.annotation.AnnotatedElementUtils
import java.lang.reflect.Method

/** Convenience shortcut for [Class.findMergedAnnotation]. */
fun <T : Annotation> Any.findMergedAnnotationOnClass(annotation: Class<out T>): T? =
	this::class.java.findMergedAnnotation(annotation)

/** Reified version of [Any.findMergedAnnotationOnClass]. */
inline fun <reified T : Annotation> Any.findMergedAnnotationOnClass(): T? =
	this::class.java.findMergedAnnotation()

/** Extension function getting a merged annotation via [AnnotatedElementUtils]. */
fun <T : Annotation> Class<*>.findMergedAnnotation(annotation: Class<out T>): T? =
	AnnotatedElementUtils.findMergedAnnotation(this, annotation)

/** Reified version of [Class.findMergedAnnotation]. */
inline fun <reified T : Annotation> Class<*>.findMergedAnnotation(): T? =
	AnnotatedElementUtils.findMergedAnnotation(this, T::class.java)

/** Extension function getting a merged annotation via [AnnotatedElementUtils]. */
fun <T : Annotation> Method.findMergedAnnotation(annotation: Class<out T>): T? =
	AnnotatedElementUtils.findMergedAnnotation(this, annotation)

/** Reified version of [Any.findMergedAnnotationOnClass]. */
inline fun <reified T : Annotation> Method.findMergedAnnotation(): T? =
	AnnotatedElementUtils.findMergedAnnotation(this, T::class.java)
