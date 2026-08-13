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

package org.elaastix.server.core

import org.assertj.core.api.Assertions.assertThat
import org.elaastix.commons.security.Role
import org.elaastix.server.sequences.SequenceEntity
import org.elaastix.server.sequences.SequenceRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import testutils.IntegrationTest
import testutils.WithMockUser

@WithMockUser(roles = [Role.WRITER])
@SpringBootTest
class AbstractEntityWithOwnershipIntegrationTest(@Autowired private val sequenceRepository: SequenceRepository) :
	IntegrationTest() {
	@Test
	fun `create a sequence without specifying the owner`() {
		val entity = sequenceRepository.persist(SequenceEntity(name = "test"))
		assertThat(entity.owner).isEqualTo(entity.creator)
	}

	@Test
	fun `create a sequence with a different owner`() {
		val otherUser = createUser()
		val entity = sequenceRepository.persist(SequenceEntity(name = "test", owner = otherUser))
		assertThat(entity.owner).isEqualTo(otherUser)
		assertThat(entity.creator).isNotEqualTo(otherUser)
	}
}
