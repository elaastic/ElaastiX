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

import org.elaastix.commons.data.Uuid
import org.elaastix.server.sequences.dto.CreateSequenceDto
import org.elaastix.server.sequences.dto.SequenceDto
import org.elaastix.server.sequences.dto.UpdateSequenceDto
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
@RequestMapping("sequences", version = "1+")
class SequenceController(private val sequenceService: SequenceService) {
	/**
	 * Create a sequence.
	 *
	 * @see [SequenceService.createSequence]
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	fun createSequence(@RequestBody body: CreateSequenceDto): SequenceDto =
		sequenceService.createSequence(body)

	/**
	 * Get all sequences.
	 *
	 * @see [SequenceService.getAllSequences]
	 */
	@GetMapping
	fun getAllSequences(pageable: Pageable): PagedModel<SequenceDto> =
		sequenceService.getAllSequences(pageable)

	/**
	 * Get a sequence.
	 *
	 * @see [SequenceService.getSequence]
	 */
	@GetMapping("{id}")
	fun getSequence(@PathVariable id: Uuid): SequenceDto =
		sequenceService.getSequence(id)

	/**
	 * Update a sequence.
	 *
	 * @see [SequenceService.updateSequence]
	 */
	@PatchMapping("{id}")
	fun updateSequence(@PathVariable id: Uuid, @RequestBody body: UpdateSequenceDto): SequenceDto =
		sequenceService.updateSequence(id, body)

	/**
	 * Delete a sequence.
	 *
	 * @see [SequenceService.deleteSequence]
	 */
	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	fun deleteSequence(@PathVariable id: Uuid) =
		sequenceService.deleteSequence(id)
}
