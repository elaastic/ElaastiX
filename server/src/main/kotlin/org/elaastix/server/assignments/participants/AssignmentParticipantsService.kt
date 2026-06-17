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

package org.elaastix.server.assignments.participants

import org.elaastix.commons.data.Uuid
import org.elaastix.commons.exceptions.ResourceNotFoundException
import org.elaastix.commons.platform.debt.SciconumTechDebt
import org.elaastix.server.assignments.AssignmentRepository
import org.elaastix.server.assignments.dto.AssignmentDto
import org.elaastix.server.users.UserRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedModel
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@OptIn(SciconumTechDebt::class)
class AssignmentParticipantsService(
	private val assignmentRepository: AssignmentRepository,
	private val userRepository: UserRepository,
) {
	/**
	 * Gets all participants to an assignment, with pagination.
	 *
	 * @param assignmentId ID of the assignment to query the participants of.
	 * @param pageable Spring's [Pageable] object for pagination.
	 * @return The result page.
	 */
	@Suppress("unused")
	@Transactional(readOnly = true)
	fun getAllParticipants(assignmentId: Uuid, pageable: Pageable): PagedModel<AssignmentDto> {
		val user = assignmentRepository.findAllParticipantsById(assignmentId, pageable)
		TODO("Not yet implemented")
	}

	/**
	 * Adds a participant to an assignment by ID.
	 *
	 * @param assignmentId The ID of the pedagogical assignment to add the user to.
	 * @param userId The ID of the user to add to the pedagogical assignment.
	 * @throws ResourceNotFoundException if the resource [assignmentId] or [userId] does not exist.
	 */
	@Transactional
	fun addParticipantToAssignmentById(assignmentId: Uuid, userId: Uuid) {
		val assignment = assignmentRepository.getReferenceById(assignmentId)
		val user = userRepository.getReferenceById(userId)
		assignment.participants.add(user)

		assignmentRepository.update(assignment)
	}

	/**
	 * Removes a participant from an assignment by its ID.
	 *
	 * @param assignmentId The ID of the assignment to remove the user from.
	 * @param userId The ID of the user to remove from the assignment.
	 */
	@Transactional
	fun removeParticipantFromAssignmentById(assignmentId: Uuid, userId: Uuid) {
		val assignment = assignmentRepository.getReferenceById(assignmentId)
		assignment.participants = assignment.participants.filter { it.id != userId }.toMutableSet()
		assignmentRepository.update(assignment)
	}

	/**
	 * Removes a set of participants from an assignment by its ID.
	 *
	 * @param assignmentId The ID of the assignment to remove the user from.
	 * @param userIds The set of user IDs to remove from the assignment.
	 */
	@Transactional
	fun removeManyParticipantFromAssignmentById(assignmentId: Uuid, userIds: Set<Uuid>) {
		val assignment = assignmentRepository.getReferenceById(assignmentId)
		assignment.participants = assignment.participants.filter { it.id !in userIds }.toMutableSet()
		assignmentRepository.update(assignment)
	}
}
