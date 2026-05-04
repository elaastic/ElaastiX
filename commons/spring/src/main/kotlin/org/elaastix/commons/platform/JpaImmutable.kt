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

package org.elaastix.commons.platform

/**
 * Annotation flagging a setter of an otherwise immutable (or managed) entity property as an implementation detail
 * imposed by JPA that SHOULD NOT be interacted with.
 *
 * Will require the use of [OptIn], or a compile-time error will be raised.
 */
@RequiresOptIn(message = "This setter is reserved for JPA and should not be used directly.")
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.PROPERTY_SETTER)
annotation class JpaImmutable
