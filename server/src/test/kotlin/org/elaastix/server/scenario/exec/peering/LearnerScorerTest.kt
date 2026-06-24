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

@file:Suppress("DestructuringDeclarationWithTooManyEntries")

package org.elaastix.server.scenario.exec.peering

import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.elaastix.commons.platform.debt.SciconumTechDebt
import org.elaastix.commons.sortedWith
import org.elaastix.server.scenario.exec.ScnConstants
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test

@SciconumTechDebt
class LearnerScorerTest {
	private fun createLearnerResponseData(correct: Boolean, confidence: UInt): ResponseData {
		require(confidence <= ScnConstants.MAX_CONFIDENCE)
		return ResponseData(correct, confidence, false, mockk(), mockk(), mockk())
	}

	private fun createData() =
		Holder8(
			createLearnerResponseData(true, 3u),
			createLearnerResponseData(true, 2u),
			createLearnerResponseData(true, 1u),
			createLearnerResponseData(true, 0u),
			createLearnerResponseData(false, 0u),
			createLearnerResponseData(false, 1u),
			createLearnerResponseData(false, 2u),
			createLearnerResponseData(false, 3u),
		)

	private fun computeScores(scoreFunction: (ResponseData) -> Int) = createData().map(scoreFunction)

	@Test
	fun `learner scores are correctly ordered`() {
		val (c3, c2, c1, c0, i0, i1, i2, i3) = computeScores(LearnerScorer::score)

		assertThat(listOf(c3, c2, c1, c0, i0, i1, i2, i3))
			.isEqualTo(listOf(4, 3, 2, 1, 0, -1, -100, -100))
	}

	@Test
	fun `learner reverse scores are correctly ordered`() {
		val (c3, c2, c1, c0, i0, i1, i2, i3) = computeScores(LearnerScorer::scoreReverse)

		assertThat(listOf(i3, i2, i1, i0, c0, c1, c2, c3))
			.isEqualTo(listOf(4, 3, 2, 1, 0, -1, -2, -3))
	}

	@Test
	fun `learner scores are correctly ordered in the correct-low order`() {
		val (c3, c2, c1, c0, i0, i1, i2, i3) = computeScores(LearnerScorer::correctLow)

		assertThat(listOf(c0, c1, c2, c3, i0, i1, i2, i3))
			.isEqualTo(listOf(4, 3, 2, 1, 0, -1, -100, -100))
	}

	@Test
	fun `learner scores are correctly ordered in the incorrect-low order`() {
		val (c3, c2, c1, c0, i0, i1, i2, i3) = computeScores(LearnerScorer::incorrectLow)

		assertThat(listOf(i0, i1, c0, c1, c2, c3, i2, i3))
			.isEqualTo(listOf(4, 3, 2, 1, 0, -1, -100, -100))
	}

	@RepeatedTest(10) // Making sure it's not a fluke
	fun `scoring functions give expected results when used with ScoreComparator`() {
		val data = createData()
		val (c3, c2, c1, c0, i0, i1, i2, i3) = data

		val shuffled = data.toList().shuffled()
		val sorted = shuffled.sortedWith(LearnerScorer::incorrectLow)
		assertThat(sorted).satisfiesAnyOf(
			// Both are acceptable since i2 and i3 are scored the same
			{ assertThat(it).isEqualTo(listOf(i0, i1, c0, c1, c2, c3, i2, i3)) },
			{ assertThat(it).isEqualTo(listOf(i0, i1, c0, c1, c2, c3, i3, i2)) },
		)
	}

	private data class Holder8<T>(
		val r1: T,
		val r2: T,
		val r3: T,
		val r4: T,
		val r5: T,
		val r6: T,
		val r7: T,
		val r8: T,
	) {
		fun toList() = listOf(r1, r2, r3, r4, r5, r6, r7, r8)
		fun <R> map(transform: (T) -> R) =
			Holder8(
				transform(r1),
				transform(r2),
				transform(r3),
				transform(r4),
				transform(r5),
				transform(r6),
				transform(r7),
				transform(r8),
			)
	}
}
