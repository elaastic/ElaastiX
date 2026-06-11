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

import org.elaastix.commons.jpa.repository.ElaastixRepository
import org.elaastix.commons.platform.debt.SciconumTechDebt
import org.elaastix.server.assignments.AssignmentEntity
import org.elaastix.server.scenario.exec.SciconumScenarioExecutionPhase
import org.elaastix.server.scenario.exec.entities.SciconumLearnerSessionEntity
import org.elaastix.server.scenario.exec.entities.SciconumSessionEntity
import org.elaastix.server.users.entities.UserEntity
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import kotlin.time.Instant

@Repository
@SciconumTechDebt
interface SciconumLearnerSessionRepository : ElaastixRepository<SciconumLearnerSessionEntity> {
	@Query(
		"FROM SciconumLearnerSessionEntity sls " +
			"WHERE sls.learner = :learner AND sls.globalSession.assignment = :assignment",
	)
	fun findAllByAssignmentAndLearner(
		assignment: AssignmentEntity,
		learner: UserEntity,
	): List<SciconumLearnerSessionEntity>

	fun findOneByGlobalSessionAndLearner(
		globalSession: SciconumSessionEntity,
		learner: UserEntity,
	): SciconumLearnerSessionEntity?

	fun findAllByGlobalSession(globalSession: SciconumSessionEntity): List<SciconumLearnerSessionEntity>

	@Modifying
	@Query(
		"UPDATE SciconumLearnerSessionEntity sls SET sls.phase = :phase, sls.nextPhaseAt = :nextPhaseAt " +
			"WHERE sls.globalSession = :globalSession AND (:phase = 'QUESTION' OR sls.phase != 'PENDING')",
	)
	fun transitionAllLearnerSessionsOfSessionTo(
		globalSession: SciconumSessionEntity,
		phase: SciconumScenarioExecutionPhase,
		nextPhaseAt: Instant?,
	): Int
}
