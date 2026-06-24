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

package org.elaastix.server.scenario.exec.peering

import org.elaastix.commons.platform.debt.SciconumTechDebt
import org.elaastix.server.activities.response.entities.ClosedResponseEntity
import org.elaastix.server.scenario.exec.ScnConstants
import org.elaastix.server.scenario.exec.entities.SciconumLearnerSessionEntity
import org.elaastix.server.users.entities.UserEntity

/**
 * This class is used to represent both the learner who answered the question, and the response itself.
 */
@SciconumTechDebt
data class ResponseData(
	val correct: Boolean,
	val confidence: UInt,
	val hasExplanation: Boolean,
	val learner: UserEntity,
	val session: SciconumLearnerSessionEntity,
	val response: ClosedResponseEntity?,
) {
	init {
		check(confidence in ScnConstants.MIN_CONFIDENCE..ScnConstants.MAX_CONFIDENCE) {
			"SCN Invariant Violation: ${ScnConstants.MIN_CONFIDENCE} <= $confidence <= ${ScnConstants.MAX_CONFIDENCE}"
		}
	}

	val isHighConfidence = confidence >= ScnConstants.HIGH_CONFIDENCE_THRESHOLD_INCL

	// This is a lot faster to compute and effectively what we need.
	// Since this calculation is going to happen within the hot loop, performance matters.
	//
	// Hash collisions aren't a correctness problem: as long as equal objects have the same hash code, we're good.
	// Collisions affect performance (since lookup is not CT without a PHF), but we'll never encounter them in our
	// use cases making this shortcut particularly relevant.
	override fun hashCode(): Int = learner.hashCode()

	override fun equals(other: Any?): Boolean =
		other is ResponseData && other.learner == learner && other.response == response
}
