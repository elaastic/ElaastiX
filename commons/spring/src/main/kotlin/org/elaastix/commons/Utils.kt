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

package org.elaastix.commons

import org.elaastix.commons.exceptions.BadRequestException
import org.elaastix.commons.exceptions.ResourceNotFoundException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Throws a [BadRequestException] if [value] is false.
 */
fun validate(value: Boolean, lazyMessage: () -> Any) {
	@OptIn(ExperimentalContracts::class)
	contract { returns() implies value }

	if (!value) {
		val message = lazyMessage()
		throw BadRequestException(message.toString())
	}
}

/**
 * Throws a [ResourceNotFoundException] if null.
 */
fun <T> T?.orNotFound(): T = this ?: throw ResourceNotFoundException()
