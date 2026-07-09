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

package org.elaastix.server.users.dtos

import kotlinx.serialization.Serializable
import org.elaastix.commons.data.Uuid
import org.elaastix.commons.security.Role
import org.elaastix.server.users.entities.UserEntity

/**
 * A user account. Holds all profile-related information and limited
 */
@Serializable
data class UserAccountDto(
	/** The user's unique ID. */
	val id: Uuid,

	val firstname: String,

	val lastname: String,

	val email: String,

	val roles: Array<Role>,
) {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as UserAccountDto

		if (id != other.id) return false
		if (firstname != other.firstname) return false
		if (lastname != other.lastname) return false
		if (email != other.email) return false
		if (!roles.contentEquals(other.roles)) return false

		return true
	}

	override fun hashCode(): Int {
		var result = id.hashCode()
		result = 31 * result + firstname.hashCode()
		result = 31 * result + lastname.hashCode()
		result = 31 * result + email.hashCode()
		result = 31 * result + roles.contentHashCode()
		return result
	}

	companion object {
		fun fromEntity(user: UserEntity): UserAccountDto =
			UserAccountDto(
				id = user.id,
				firstname = user.firstName,
				lastname = user.lastName,
				email = user.email ?: "",
				roles = user.roles.toTypedArray(),
			)
	}
}
