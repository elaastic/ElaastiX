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

package org.elaastix.server.scenario.exec.repositories

import org.elaastix.commons.data.Uuid
import org.elaastix.commons.jpa.repository.ElaastixRepository
import org.elaastix.commons.platform.debt.SciconumTechDebt
import org.elaastix.server.assignments.AssignmentEntity
import org.elaastix.server.scenario.exec.SciconumScenarioExecutionPhase
import org.elaastix.server.scenario.exec.entities.SciconumLearnerSessionEntity
import org.elaastix.server.scenario.exec.entities.SciconumScenarioSessionEntity
import org.elaastix.server.users.entities.UserEntity
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import kotlin.time.Instant

@Repository
@SciconumTechDebt
interface SciconumLearnerSessionRepository : ElaastixRepository<SciconumLearnerSessionEntity> {
	fun findByIdAndLearner(id: Uuid, learner: UserEntity): SciconumLearnerSessionEntity?

	@Query("FROM SciconumLearnerSessionEntity sls WHERE sls.learner = :learner AND sls.phase != 'END'")
	fun findAllByLearnerAndNotEnded(learner: UserEntity): List<SciconumLearnerSessionEntity>

	@Query(
		"FROM SciconumLearnerSessionEntity sls " +
			"WHERE sls.learner = :learner AND sls.scenarioSession.assignment = :assignment",
	)
	fun findAllByAssignmentAndLearner(
		assignment: AssignmentEntity,
		learner: UserEntity,
	): List<SciconumLearnerSessionEntity>

	fun findOneByScenarioSessionAndLearner(
		globalSession: SciconumScenarioSessionEntity,
		learner: UserEntity,
	): SciconumLearnerSessionEntity?

	fun findAllByScenarioSession(globalSession: SciconumScenarioSessionEntity): List<SciconumLearnerSessionEntity>

	/**
	 * Transitions a list of learner sessions identified by their id to a given phase.
	 */
	@Modifying
	@Query(
		"UPDATE SciconumLearnerSessionEntity sls SET sls.phase = :phase, sls.nextPhaseAt = :nextPhaseAt " +
			"WHERE sls.id IN :sessions",
	)
	fun transitionAllToPhase(sessions: List<Uuid>, phase: SciconumScenarioExecutionPhase, nextPhaseAt: Instant?): Int

	/**
	 * Returns the UUIDs of all learner sessions attached to a given scenario session.
	 * Implementation note: there id an issue related to Hibernate here that prevents typing List<Uuid>. As a workaround,
	 * we use the type Any that can be safely cast to Uuid by the caller.
	 * @param scenarioSession The scenario session.
	 */
	@Query(
		"SELECT sls.id FROM SciconumLearnerSessionEntity sls " +
			"WHERE sls.scenarioSession = :scenarioSession",
	)
	fun findAllIdsByScenarioSession(scenarioSession: SciconumScenarioSessionEntity): List<Any>

	/**
	 * Returns the UUIDs of all learner sessions attached to a given scenario session that are not in a given phase.
	 * Implementation note: there id an issue related to Hibernate here that prevents typing List<Uuid>. As a workaround,
	 * we use the type Any that can be safely cast to Uuid by the caller.
	 * @param scenarioSession The scenario session.
	 * @param phase The phase to exclude.
	 */
	@Query(
		"SELECT sls.id FROM SciconumLearnerSessionEntity sls " +
			"WHERE sls.scenarioSession = :scenarioSession AND sls.phase != :phase",
	)
	fun findAllIdsByScenarioSessionAndPhaseNot(
		scenarioSession: SciconumScenarioSessionEntity,
		phase: SciconumScenarioExecutionPhase,
	): List<Any>
}
