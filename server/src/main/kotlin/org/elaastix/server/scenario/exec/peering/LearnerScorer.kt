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
import org.elaastix.server.scenario.exec.ScnConstants

private const val LARGE = 100

/**
 * Namespace onto which the scoring functions for a learner based on their response lives.
 */
@SciconumTechDebt
object LearnerScorer {
	/**
	 * Gives to a learner a score based on the correctness of their answer to the response and their confidence.
	 * Incorrect learners with a high confidence are strongly weighted down.
	 */
	fun score(learner: ResponseData) =
		when {
			learner.correct -> learner.confidence.toInt() + 1
			learner.isHighConfidence -> -LARGE
			else -> -learner.confidence.toInt()
		}

	/**
	 * Gives to a learner a score based on the correctness of their answer to the response and their confidence.
	 * Learners are scored in a reverse order (lowest to highest) with no strong weighting.
	 */
	fun scoreReverse(learner: ResponseData) =
		when {
			learner.correct -> -learner.confidence.toInt()
			else -> learner.confidence.toInt() + 1
		}

	/**
	 * Scoring function that favours correct learners, particularly those with low confidence.
	 * The purpose of this arrangement is to prioritise learners that aren't already prioritised for reviewing bad
	 * answers.
	 *
	 * Incorrect learners with a high confidence are strongly weighted down.
	 */
	fun correctLow(learner: ResponseData) =
		when {
			learner.correct -> (ScnConstants.MAX_CONFIDENCE + 1u).toInt() - learner.confidence.toInt()
			learner.isHighConfidence -> -LARGE
			else -> -learner.confidence.toInt()
		}

	/**
	 * Scoring function that favours incorrect learners with low confidence, particularly those with low confidence.
	 * The purpose of this arrangement is to prioritise the "middle of the pack", with an emphasis on incorrect
	 * learners.
	 *
	 * Incorrect learners with a high confidence are strongly weighted down.
	 */
	fun incorrectLow(learner: ResponseData) =
		when {
			!learner.correct && learner.isHighConfidence -> -LARGE

			!learner.correct ->
				(ScnConstants.MAX_CONFIDENCE - learner.confidence).toInt() +
					ScnConstants.HIGH_CONFIDENCE_THRESHOLD_INCL.toInt() - 1

			else ->
				(ScnConstants.MAX_CONFIDENCE - learner.confidence).toInt() -
					ScnConstants.HIGH_CONFIDENCE_THRESHOLD_INCL.toInt() + 1
		}
}
