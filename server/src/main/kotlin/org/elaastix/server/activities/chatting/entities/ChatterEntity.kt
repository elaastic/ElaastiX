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

package org.elaastix.server.activities.chatting.entities

import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne
import jakarta.validation.constraints.NotNull
import org.elaastix.commons.jpa.entity.AbstractMinimalEntity
import org.elaastix.server.users.entities.UserEntity

/**
 * A "throwaway" identity used to anonymise chatters whilst ensuring they have a consistent identity during
 * the activity.
 *
 * Each identity is effectively *single-use*, and a new identity should be generated when creating a new chat-based
 * activity.
 */
@Entity
class ChatterEntity(
	/**
	 * The actual chatter's identity.
	 */
	@NotNull
	@ManyToOne
	var chatter: UserEntity,

	/**
	 * The nickname used for this throwaway identity.
	 */
	@NotNull
	var nickname: String,
) : AbstractMinimalEntity()
