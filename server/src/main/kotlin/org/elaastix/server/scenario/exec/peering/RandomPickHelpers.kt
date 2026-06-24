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

import org.elaastix.commons.ScoreComparator
import org.elaastix.commons.platform.debt.SciconumTechDebt
import org.elaastix.server.activities.response.entities.ClosedResponseEntity
import org.elaastix.server.scenario.exec.ScnConstants
import org.elaastix.server.users.entities.UserEntity
import java.util.Random
import java.util.SortedSet
import kotlin.collections.getValue

private typealias LRMap = Map<UserEntity, MutableSet<ClosedResponseEntity>>

@SciconumTechDebt
private fun Set<ClosedResponseEntity>.checkAccepts(rd: ResponseData) =
	size < ScnConstants.ASSESSED_COUNT.toInt() && !contains(rd.response)

@SciconumTechDebt // Relies on SCN invariant
@Suppress("MagicNumber")
private fun Int.toAbsScore(): UInt =
	(-(this - 4)).toUInt()

/**
 * Picks a random entry of the set, using an algorithm where each entry is twice less likely to be picked than the last.
 * The randomisation is also influenced by whether the learner already has responses assigned to them, proportionally
 * to the number of already attributed responses over the total number of responses to assign.
 *
 * In other words, this picks an entry from the set that has ~50% chance of being the first, ~25% of being the second,
 * 17.5% chance of being the third, etc. This allows for balancing curation quality and making sure the selection is
 * nondeterministic.
 *
 * Consumers of this method SHOULD use a cryptographically secure random number generator. While a decently seeded
 * PRNG could suffice, the guarantees implied by a PRNG intended for cryptographic use makes it extremely desirable
 * when fairness is expected.
 */
@SciconumTechDebt
@Suppress("ReturnCount")
fun SortedSet<ResponseData>.pickRandomReviewer(
	peering: Map<UserEntity, MutableSet<ClosedResponseEntity>>,
	random: Random,
): ResponseData? {
	check(isNotEmpty())

	@Suppress("UNCHECKED_CAST") // It's ugly but it's quick.
	val comparator = comparator() as ScoreComparator<ResponseData, Int>
	val iterator = iterator()

	var found: Boolean
	var response: ResponseData
	var assigned: Set<ClosedResponseEntity>
	var score: UInt

	do {
		response = iterator.next()
		assigned = peering.getValue(response.learner)
		found = assigned.checkAccepts(response)
	} while (!found && iterator.hasNext())

	if (!found) {
		// No eligible learner for this response. Odd, but whatever.
		return null
	}

	score = comparator.scoring(response).toAbsScore()

	while (iterator.hasNext()) {
		val next = iterator.next()
		val nextAssigned = peering.getValue(next.learner)
		if (!nextAssigned.checkAccepts(next)) continue

		// positive weight -> penalty
		// negative weight -> advantage

		// +10% chance per score point
		val scoreWeight = score.toInt() * -0.1

		// If the current learner already has 1 slot filled but not the next one, -10% chance
		// If that's the other way around, +10% chance
		val assignWeight = 0.1 * (assigned.size - nextAssigned.size)

		// +10% chance per score point difference with the next entry
		val nextScore = comparator.scoring(response).toAbsScore() // Next score must be GTE.
		val nextWeight = 0.1 * (score - nextScore).toInt()

		// Base chance for current to be picked: 30%
		val minRollToWin = 0.7 + scoreWeight + assignWeight + nextWeight

		val roll = random.nextDouble() // [0, 1[
		if (roll >= minRollToWin) {
			return response
		}

		response = next
		assigned = nextAssigned
		score = nextScore
	}

	// In case we didn't return -- unlikely but not impossible.
	return response
}
