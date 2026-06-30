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
import org.elaastix.server.scenario.exec.entities.SciconumChatPeeringEntity
import org.elaastix.server.scenario.exec.entities.SciconumLearnerSessionEntity
import org.elaastix.server.scenario.exec.entities.SciconumScenarioSessionEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
@SciconumTechDebt
interface SciconumChatPeeringRepository : ElaastixRepository<SciconumChatPeeringEntity> {
	@Query(
		"FROM SciconumChatPeeringEntity cpe " +
			"INNER JOIN SciconumLearnerSessionEntity lse ON lse.scenarioSession = cpe.scenarioSession " +
			"INNER JOIN SciconumChatterEntity ce ON ce.learner = lse.learner AND ce.peering = cpe " +
			"WHERE lse = :session AND cpe.sessionRound = lse.scenarioSession.currentRound",
	)
	fun findCurrentOneByLearnerSession(session: SciconumLearnerSessionEntity): SciconumChatPeeringEntity?

	fun findAllByScenarioSession(session: SciconumScenarioSessionEntity): List<SciconumChatPeeringEntity>
}
