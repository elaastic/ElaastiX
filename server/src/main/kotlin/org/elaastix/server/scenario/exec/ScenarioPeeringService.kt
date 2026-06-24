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

package org.elaastix.server.scenario.exec

import org.apache.commons.logging.LogFactory
import org.elaastix.commons.platform.debt.SciconumTechDebt
import org.elaastix.commons.platform.wip.UnclearAuthorshipOwnership
import org.elaastix.commons.toFixed
import org.elaastix.commons.toSortedSet
import org.elaastix.mm.activity.AbsoluteGradable
import org.elaastix.server.activities.response.entities.ClosedResponseEntity
import org.elaastix.server.scenario.exec.entities.SciconumChatPeeringEntity
import org.elaastix.server.scenario.exec.entities.SciconumJudgePeeringEntity
import org.elaastix.server.scenario.exec.entities.SciconumLearnerSessionEntity
import org.elaastix.server.scenario.exec.entities.SciconumScenarioSessionEntity
import org.elaastix.server.scenario.exec.peering.LearnerScorer
import org.elaastix.server.scenario.exec.peering.ResponseData
import org.elaastix.server.scenario.exec.peering.pickRandomReviewer
import org.elaastix.server.scenario.exec.repositories.SciconumChatPeeringRepository
import org.elaastix.server.scenario.exec.repositories.SciconumJudgePeeringRepository
import org.elaastix.server.scenario.exec.repositories.SciconumLearnerSessionRepository
import org.elaastix.server.users.entities.UserEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.security.SecureRandom

@Service
@SciconumTechDebt
@OptIn(UnclearAuthorshipOwnership::class)
@Transactional(propagation = Propagation.MANDATORY)
class ScenarioPeeringService(
	private val sciconumJudgePeeringRepository: SciconumJudgePeeringRepository,
	private val sciconumChatPeeringRepository: SciconumChatPeeringRepository,
	private val sciconumLearnerSessionRepository: SciconumLearnerSessionRepository,
	private val random: SecureRandom,
) {
	companion object {
		private val LOGGER = LogFactory.getLog(ScenarioPeeringService::class.java)
	}

	fun assignPeerResponses(
		session: SciconumScenarioSessionEntity,
		responses: List<ClosedResponseEntity>,
	): List<SciconumJudgePeeringEntity>? =
		performAssignment(session, responses, ::performPeerAssessmentAssignment)?.let {
			sciconumJudgePeeringRepository.persistAll(it)
		}

	fun assignPeerChatters(
		session: SciconumScenarioSessionEntity,
		responses: List<ClosedResponseEntity>,
	): List<SciconumChatPeeringEntity>? =
		performAssignment(session, responses, ::performChattersAssignment)?.let {
			sciconumChatPeeringRepository.persistAll(it)
		}

	private typealias AlgorithmFn<R> = (
		session: SciconumScenarioSessionEntity,
		learnerSessions: List<SciconumLearnerSessionEntity>,
		learnerResponseData: List<ResponseData>,
	) -> R

	private final fun <T> performAssignment(
		session: SciconumScenarioSessionEntity,
		responses: List<ClosedResponseEntity>,
		algorithm: AlgorithmFn<T>,
	): T? {
		val learnerSessions = sciconumLearnerSessionRepository.findAllByScenarioSession(session)
		val correctResponses = responses.filter { it.absoluteGrade == AbsoluteGradable.AbsoluteGrade.PASS }
		val correctResponsesShare = correctResponses.size.toDouble() / learnerSessions.size.toDouble()

		if (correctResponsesShare !in ScnConstants.MIN_CORRECT_SHARE..ScnConstants.MAX_CORRECT_SHARE) {
			LOGGER.warn(
				"Peer allocation bailout: share of correct answer falls outside of the safe range. " +
					"${ScnConstants.MIN_CORRECT_SHARE.toFixed(2u)} < " +
					"${correctResponsesShare.toFixed(2u)} < " +
					ScnConstants.MAX_CORRECT_SHARE.toFixed(2u),
			)

			return null
		}

		val learnerResponseMap = responses.associateBy { it.author }
		val responseData = learnerSessions.map { learnerResponseMap[it.learner].toData(it) }

		return algorithm(session, learnerSessions, responseData)
	}

	private final fun performPeerAssessmentAssignment(
		session: SciconumScenarioSessionEntity,
		learnerSessions: List<SciconumLearnerSessionEntity>,
		learnerResponseData: List<ResponseData>,
	): List<SciconumJudgePeeringEntity> {
		val peering = mutableMapOf<UserEntity, MutableSet<ClosedResponseEntity>>()
			.withDefault { mutableSetOf() }

		// Responses are processed in a random order to have a slightly better distribution
		val resp = learnerResponseData.mapNotNull { it.response?.let { r -> it to r } }
		do {
			// Responses are shuffled before being sorted to make ties random.
			// A more robust impl would reinject entropy each time a learner has been picked, but this requires a more
			// sophisticated solution.
			//
			// A ScoreSortedSetWithRandomisedTiebreak would store learners with the same score in a list, and when
			// picking it'd randomise the subsets prior to iterating.
			//
			// (the map would need to be sorted too!)
			// Map<Int, MutableSet> setsByScore = mutableMapOf().withDefault { mutableSetOf() }
			//
			// Iterator:
			//   for (subset in setsByScore.values)
			//      shuffledSubset = subset.shuffled()
			//      for (item in shuffledSubset)
			//        ...
			val strongLearners by lazy { learnerResponseData.shuffled().toSortedSet(LearnerScorer::score) }
			val correctLearners by lazy { learnerResponseData.shuffled().toSortedSet(LearnerScorer::correctLow) }
			val incorrectLearners by lazy { learnerResponseData.shuffled().toSortedSet(LearnerScorer::incorrectLow) }
			val toxicLearners by lazy { learnerResponseData.shuffled().toSortedSet(LearnerScorer::scoreReverse) }

			for ((response, responseEntity) in resp.shuffled(random)) {
				val orderedReviewerPool =
					when {
						response.correct && response.hasExplanation -> toxicLearners
						response.correct && !response.hasExplanation -> incorrectLearners
						!response.correct && !response.hasExplanation -> correctLearners
						else -> strongLearners
					}

				orderedReviewerPool.pickRandomReviewer(peering, random)?.let { reviewer ->
					val assigned = peering.getValue(reviewer.learner)
					if (assigned.size < ScnConstants.ASSESSED_COUNT.toInt()) {
						// This should in practice never happen; but in the event there's too many answers (??????)
						// we'd end up here. Gracefully exit if it ever occurs.
						LOGGER.warn(
							"Peer assessment allocation aborted for session ${session.id}: " +
								"not enough learners to assign all responses?!",
						)
						break
					}

					assigned.add(responseEntity)
				}
			}
		} while (learnerResponseData.any { peering.getValue(it.learner).size < ScnConstants.ASSESSED_COUNT.toInt() })

		return learnerSessions.map { ls ->
			val responses = peering.getValue(ls.learner)
			check(responses.size == ScnConstants.ASSESSED_COUNT.toInt())

			SciconumJudgePeeringEntity(
				learnerSession = ls,
				responses = responses,
				sessionRound = session.currentRound,
			)
		}
	}

	@Suppress("UnusedParameter")
	private final fun performChattersAssignment(
		session: SciconumScenarioSessionEntity,
		learnerSessions: List<SciconumLearnerSessionEntity>,
		learnerResponseData: List<ResponseData>,
	): List<SciconumChatPeeringEntity> {
		TODO()
	}

	/**
	 * Establish the profile of a learner's response.
	 * If no response is provided, it is assumed to be incorrect and of low confidence.
	 */
	private final fun ClosedResponseEntity?.toData(session: SciconumLearnerSessionEntity) =
		ResponseData(
			response = this,
			correct = this?.absoluteGrade == AbsoluteGradable.AbsoluteGrade.PASS,
			hasExplanation = this?.selfExplanation?.isNotBlank() == true,
			confidence = when (this) {
				null -> 0u
				else -> checkNotNull(this.confidenceDegree) { "SCN Invariant Violation: confidence degree is null" }
			},
			learner = session.learner,
			session = session,
		)
}
