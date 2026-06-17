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

import jakarta.validation.Valid
import jakarta.validation.ValidationException
import org.elaastix.commons.data.Uuid
import org.elaastix.commons.exceptions.ResourceNotFoundException
import org.elaastix.commons.orNotFound
import org.elaastix.commons.platform.debt.SciconumTechDebt
import org.elaastix.commons.platform.wip.UnclearAuthorshipOwnership
import org.elaastix.commons.toRefList
import org.elaastix.commons.toTypedRefList
import org.elaastix.commons.toUuidSet
import org.elaastix.server.assignments.dto.AssignmentDto
import org.elaastix.server.assignments.dto.CreateAssignmentDto
import org.elaastix.server.assignments.dto.UpdateAssignmentDto
import org.elaastix.server.assignments.event.AssignmentCreateEvent
import org.elaastix.server.sequences.SciconumSequenceEntity
import org.elaastix.server.sequences.SequenceEntity
import org.elaastix.server.sequences.SequenceRepository
import org.elaastix.server.sequences.SequenceService.Companion.toDto
import org.springframework.context.ApplicationEventPublisher
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedModel
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AssignmentService(
	private val applicationEventPublisher: ApplicationEventPublisher,
	private val assignmentRepository: AssignmentRepository,
	private val sequenceRepository: SequenceRepository,
) {
	companion object {
		/** Maps an [AssignmentEntity] to an [AssignmentDto]. */
		@OptIn(SciconumTechDebt::class, UnclearAuthorshipOwnership::class)
		fun AssignmentEntity.toDto(): AssignmentDto =
			AssignmentDto(
				id = id,
				displayName = displayName,
				sequences = sequences.map { it.toDto() },
				participantIds = participants.toUuidSet(),
				creatorId = creator.id,
			)
	}

	/**
	 * Gets all assignments, with pagination.
	 *
	 * @param pageable Spring's [Pageable] object for pagination.
	 * @return The result page.
	 */
	@Suppress("unused")
	@Transactional(readOnly = true)
	fun getAllAssignments(pageable: Pageable): PagedModel<AssignmentDto> {
		TODO("Not yet implemented")
	}

	/**
	 * Retrieves an assignment by its ID.
	 *
	 * @param id The ID to fetch.
	 * @throws ResourceNotFoundException if the resource does not exist.
	 * @return The requested assignment.
	 */
	@Transactional(readOnly = true)
	fun getAssignment(id: Uuid): AssignmentDto = assignmentRepository.findById(id).orNotFound().toDto()

	/**
	 * Creates a new assignment.
	 *
	 * @param dto The creation payload. Will be validated.
	 * @throws ValidationException if the [dto] is invalid.
	 * @return The created entity.
	 */
	@Transactional
	fun createAssignment(@Valid dto: CreateAssignmentDto): AssignmentDto {
		@OptIn(SciconumTechDebt::class)
		val sequences = dto.sequenceIds.toTypedRefList<SciconumSequenceEntity, _>(sequenceRepository)

		val entity = assignmentRepository.persist(
			@Suppress("UNCHECKED_CAST") // Workaround that is safe to do here (we're widening the type)
			@OptIn(SciconumTechDebt::class)
			AssignmentEntity(
				displayName = dto.displayName,
				sequences = sequences as MutableList<SequenceEntity>,
				participants = mutableSetOf(),
			),
		)

		applicationEventPublisher.publishEvent(AssignmentCreateEvent(this, entity))

		return entity.toDto()
	}

	/**
	 * Updates an assignment by its ID.
	 *
	 * @param id The ID to update.
	 * @param dto The update payload. Will be validated.
	 * @throws ResourceNotFoundException if the resource [id] does not exist.
	 * @throws ValidationException if the [dto] is invalid.
	 * @return The updated entity.
	 */
	@Transactional
	fun updateAssignment(id: Uuid, @Valid dto: UpdateAssignmentDto): AssignmentDto {
		val entity = assignmentRepository.findByIdAndUpdate(id) {
			dto.displayName.takeIfUpdated { displayName = it }
			dto.sequenceIds.takeIfUpdated { sequences = it.toRefList(sequenceRepository) }
		}

		return entity.orNotFound().toDto()
	}

	/**
	 * Deletes an assignment by its ID.
	 *
	 * @param id The ID to delete.
	 * @throws DataIntegrityViolationException if a foreign key constraint fails.
	 */
	@Transactional
	fun deleteAssignment(id: Uuid) {
		assignmentRepository.deleteById(id)
	}
}
