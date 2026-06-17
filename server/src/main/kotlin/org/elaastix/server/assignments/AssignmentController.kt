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

package org.elaastix.server.assignments

import org.elaastix.commons.data.Uuid
import org.elaastix.server.assignments.dto.AssignmentDto
import org.elaastix.server.assignments.dto.CreateAssignmentDto
import org.elaastix.server.assignments.dto.UpdateAssignmentDto
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedModel
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("assignments", version = "1+")
class AssignmentController(private val assignmentService: AssignmentService) {
	/**
	 * Create an assignment.
	 *
	 * @see [AssignmentService.createAssignment]
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	fun createAssignment(@RequestBody body: CreateAssignmentDto): AssignmentDto =
		assignmentService.createAssignment(body)

	/**
	 * Get all assignments.
	 *
	 * @see [AssignmentService.getAllAssignments]
	 */
	@GetMapping
	fun getAllAssignments(pageable: Pageable): PagedModel<AssignmentDto> =
		assignmentService.getAllAssignments(pageable)

	/**
	 * Get an assignment.
	 *
	 * @see [AssignmentService.getAssignment]
	 */
	@GetMapping("{id}")
	fun getAssignment(@PathVariable id: Uuid): AssignmentDto =
		assignmentService.getAssignment(id)

	/**
	 * Update an assignment.
	 *
	 * @see [AssignmentService.updateAssignment]
	 */
	@PatchMapping("{id}")
	fun updateAssignment(@PathVariable id: Uuid, @RequestBody body: UpdateAssignmentDto): AssignmentDto =
		assignmentService.updateAssignment(id, body)

	/**
	 * Delete an assignment.
	 *
	 * @see [AssignmentService.deleteAssignment]
	 */
	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	fun deleteAssignment(@PathVariable id: Uuid) =
		assignmentService.deleteAssignment(id)
}
