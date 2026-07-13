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

package org.elaastix.server.activities.chatting.repositories

import org.elaastix.commons.jpa.repository.ElaastixRepository
import org.elaastix.commons.platform.debt.SciconumTechDebt
import org.elaastix.server.activities.chatting.entities.ChatterEntity
import org.elaastix.server.scenario.exec.entities.SciconumChatPeeringEntity
import org.elaastix.server.users.entities.UserEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ChatterRepository : ElaastixRepository<ChatterEntity> {
	@SciconumTechDebt
	@Query(
		"SELECT c FROM SciconumChatPeeringEntity p " +
			"INNER JOIN p.chatters c " +
			"WHERE p = :peering AND c.chatter = :chatter",
	)
	fun findChatterIdentityOfUserInPeering(chatter: UserEntity, peering: SciconumChatPeeringEntity): ChatterEntity?
}
