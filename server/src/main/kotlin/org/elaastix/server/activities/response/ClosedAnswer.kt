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

package org.elaastix.server.activities.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Sum type of possible responses to a closed answer.
 */
@Serializable
sealed interface ClosedAnswer {
	/**
	 * Response to a single-choice closed question.
	 */
	@Serializable
	@SerialName("Single")
	data class Single(
		/** The underlying value; an index into the question's choices list. */
		val value: UInt?,
	) : ClosedAnswer

	/**
	 * Response to a multiple-choice closed question.
	 */
	@Serializable
	@SerialName("Multiple")
	data class Multiple(
		/** The underlying value; a list of indices into the question's choices list. */
		val value: Set<UInt>,
	) : ClosedAnswer
}
