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

import org.elaastix.commons.platform.debt.SciconumTechDebt
import org.elaastix.server.assignments.event.AssignmentCreateEvent
import org.elaastix.server.assignments.event.AssignmentJoinEvent
import org.elaastix.server.scenario.exec.entities.SciconumLearnerSessionEntity
import org.elaastix.server.scenario.exec.entities.SciconumScenarioSessionEntity
import org.elaastix.server.scenario.exec.repositories.SciconumLearnerSessionRepository
import org.elaastix.server.scenario.exec.repositories.SciconumScenarioSessionRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
@SciconumTechDebt
class SciconumScenarioSessionCreator(
	private val scenarioSessionRepository: SciconumScenarioSessionRepository,
	private val learnerSessionRepository: SciconumLearnerSessionRepository,
) {
	@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
	fun handleLearnerJoin(event: AssignmentJoinEvent) {
		val sessions = scenarioSessionRepository.findAllByAssignment(event.assignment)
		check(sessions.isNotEmpty())

		for (session in sessions) {
			if (session.phase == SciconumScenarioExecutionPhase.END) continue

			learnerSessionRepository.persist(
				SciconumLearnerSessionEntity(
					scenarioSession = session,
					learner = event.learner,
					// If scenario is in a different state, it means the learner is late.
					// They'll transition on the next round and wait in the meantime.
					phase = SciconumScenarioExecutionPhase.PENDING,
					nextPhaseAt = null,
				),
			)
		}
	}

	@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
	fun handleAssignmentCreation(event: AssignmentCreateEvent) {
		val sequences = event.assignment.sequences
		check(sequences.isNotEmpty())

		for (sequence in sequences) {
			scenarioSessionRepository.persist(
				SciconumScenarioSessionEntity(
					assignment = event.assignment,
					sequence = sequence,
					phase = SciconumScenarioExecutionPhase.PENDING,
					currentRound = 0u,
					nextPhaseAt = null,
				),
			)
		}
	}
}
