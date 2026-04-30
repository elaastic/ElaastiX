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

package org.elaastix.server.activities.response.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.elaastix.commons.data.Uuid
import org.elaastix.mm.content.FormattedContent
import org.elaastix.server.activities.response.CLOSED_RESPONSE_DISCRIMINATOR
import org.elaastix.server.activities.response.ClosedAnswer

/** A response to an open question. */
@Serializable
@SerialName(CLOSED_RESPONSE_DISCRIMINATOR)
data class ClosedResponseDto(
	/** Unique ID of the response. */
	val id: Uuid,
	/** ID of the user who authored the response. */
	val authorId: Uuid,
	/** ID of the question this answer is attached to. */
	val questionId: Uuid,
	/** ID of the response being amended, if any. */
	val amendedResponse: Uuid?,
	/** Response contents. */
	val answer: ClosedAnswer,
	/** The explanation of the user for their answer. Optional. */
	val selfExplanation: FormattedContent?,
	/** The confidence degree of the user for their answer. Optional. */
	val confidenceDegree: UInt?,
) : ResponseDto
