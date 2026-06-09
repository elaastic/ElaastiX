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

package org.elaastix.commons.jpa.event

import org.elaastix.commons.jpa.entity.AbstractEntity
import org.springframework.context.ApplicationEvent
import org.springframework.core.ResolvableType
import org.springframework.core.ResolvableTypeProvider

/**
 * Generic event class for entity-related events. Implements the necessary interfaces for generic type resolution.
 *
 * @param T The entity's type. Will be used to discriminate event listeners appropriately.
 * @param source The object on which the event initially occurred or with which the event is associated.
 * @property entity The entity.
 */
sealed class AbstractEntityEvent<T : AbstractEntity>(source: Any, val entity: T) :
	ApplicationEvent(source),
	ResolvableTypeProvider {
	override fun getResolvableType() =
		ResolvableType.forClassWithGenerics(
			this::class.java,
			ResolvableType.forInstance(entity),
		)
}
