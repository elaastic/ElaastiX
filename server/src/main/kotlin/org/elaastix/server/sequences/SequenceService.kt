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

package org.elaastix.server.sequences

import jakarta.validation.Valid
import jakarta.validation.ValidationException
import org.elaastix.commons.data.Uuid
import org.elaastix.commons.exceptions.ResourceNotFoundException
import org.elaastix.commons.orNotFound
import org.elaastix.commons.platform.debt.SciconumTechDebt
import org.elaastix.commons.platform.wip.UnclearAuthorshipOwnership
import org.elaastix.commons.toRefSet
import org.elaastix.server.activities.response.ResponseActivityService.Companion.toDto
import org.elaastix.server.activities.response.entities.QuestionEntity
import org.elaastix.server.activities.response.entities.projections.QuestionStatementProjection
import org.elaastix.server.activities.response.repositories.QuestionRepository
import org.elaastix.server.sequences.dto.CreateSequenceDto
import org.elaastix.server.sequences.dto.SequenceDto
import org.elaastix.server.sequences.dto.UpdateSequenceDto
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedModel
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SequenceService(
	private val sequenceRepository: SequenceRepository,
	@property:SciconumTechDebt private val questionRepository: QuestionRepository,
) {
	companion object {
		/** Maps a [SequenceEntity] to a [SequenceDto]. */
		@OptIn(SciconumTechDebt::class, UnclearAuthorshipOwnership::class)
		fun SequenceEntity.toDto(): SequenceDto =
			SequenceDto(
				id = id,
				name = name,
				sciconumScenario = sciconumScenario,
				sciconumQuestions = sciconumQuestions.map { it.toDto() }.toSet(),
				ownerId = owner.id,
			)

		@SciconumTechDebt
		fun QuestionEntity.toDto() =
			QuestionStatementProjection.from(this).toDto()
	}

	/**
	 * Gets all sequences, with pagination.
	 *
	 * @param pageable Spring's [Pageable] object for pagination.
	 * @return The result page.
	 */
	@Suppress("unused")
	@Transactional(readOnly = true)
	fun getAllSequences(pageable: Pageable): PagedModel<SequenceDto> {
		TODO("Not yet implemented")
	}

	/**
	 * Retrieves a sequence by its ID.
	 *
	 * @param id The ID to fetch.
	 * @throws ResourceNotFoundException if the resource does not exist.
	 * @return The requested sequence.
	 */
	@Transactional(readOnly = true)
	fun getSequence(id: Uuid): SequenceDto = sequenceRepository.findById(id).orNotFound().toDto()

	/**
	 * Creates a new sequence.
	 *
	 * @param dto The creation payload. Will be validated.
	 * @throws ValidationException if the [dto] is invalid.
	 * @throws ResourceNotFoundException if the target question does not exist.
	 * @return The created entity.
	 */
	@Transactional
	fun createSequence(@Valid dto: CreateSequenceDto): SequenceDto {
		val entity = sequenceRepository.persist(
			@OptIn(SciconumTechDebt::class)
			SequenceEntity(
				name = dto.name,
				sciconumScenario = dto.sciconumScenario,
				sciconumQuestions = dto.sciconumQuestionIds.toRefSet(questionRepository),
			),
		)

		return entity.toDto()
	}

	/**
	 * Updates a sequence by its ID.
	 *
	 * @param id The ID to update.
	 * @param dto The update payload. Will be validated.
	 * @throws ResourceNotFoundException if the resource [id] does not exist or the target question does not.
	 * @throws ValidationException if the [dto] is invalid.
	 * @return The updated entity.
	 */
	@Transactional
	fun updateSequence(id: Uuid, @Valid dto: UpdateSequenceDto): SequenceDto {
		val entity = sequenceRepository.findByIdAndUpdate(id) {
			dto.name.takeIfUpdated { name = it }

			@OptIn(SciconumTechDebt::class)
			dto.sciconumScenario.takeIfUpdated { sciconumScenario = it }

			@OptIn(SciconumTechDebt::class)
			dto.sciconumQuestionId.takeIfUpdated { sciconumQuestions = it.toRefSet(questionRepository) }
		}

		return entity.orNotFound().toDto()
	}

	/**
	 * Deletes a sequence by its ID.
	 *
	 * @param id The ID to delete.
	 * @throws DataIntegrityViolationException if a foreign key constraint fails.
	 */
	@Transactional
	fun deleteSequence(id: Uuid) {
		sequenceRepository.deleteById(id)
	}
}
