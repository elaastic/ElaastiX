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

import kotlinx.serialization.Serializable
import org.elaastix.commons.data.Uuid
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedModel
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@Suppress("UnusedParameter")
@RequestMapping("assignments/{assignmentId}/participants", version = "1+")
class AssignmentParticipantsController(private val assignmentParticipantsService: AssignmentParticipantsService) {
	/**
	 * Adds a participant to the assignment.
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	fun addParticipant(
		@PathVariable assignmentId: Uuid,
		@RequestBody body: AddParticipantDto,
	) =
		assignmentParticipantsService.addParticipantToAssignmentById(assignmentId, body.userId)

	/**
	 * Lists all participants to an assignment.
	 *
	 * @see [AssignmentParticipantsService.getAllParticipants]
	 */
	@GetMapping
	fun getAllParticipants(
		@PathVariable assignmentId: Uuid,
		pageable: Pageable,
	): PagedModel<Nothing> =
		TODO("Not yet implemented")

	/**
	 * Removes a participant from the assignment.
	 */
	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	fun removeParticipant(
		@PathVariable assignmentId: Uuid,
		@PathVariable id: Uuid,
	) =
		assignmentParticipantsService.removeParticipantFromAssignmentById(assignmentId, id)

	/**
	 * Removes many participants from the assignment.
	 */
	@DeleteMapping("bulk")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	fun removeParticipantsBulk(
		@PathVariable assignmentId: Uuid,
		@RequestBody ids: Set<Uuid>,
	) =
		assignmentParticipantsService.removeManyParticipantFromAssignmentById(assignmentId, ids)

	/**
	 * Request payload for adding a participant to a pedagogical assignment.
	 *
	 * @property userId The ID of the user to add.
	 */
	@Serializable
	data class AddParticipantDto(val userId: Uuid)
}
