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

package org.elaastix.server.users.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.elaastix.commons.jpa.entity.AbstractEntity
import org.elaastix.commons.security.Role
import org.elaastix.mm.users.User
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

/**
 * @see [User]
 */
@Entity
@Table(name = "users")
class UserEntity(
	@NotBlank
	@NotNull
	@Size(max = 256)
	override var firstName: String,

	@NotBlank
	@NotNull
	@Size(max = 256)
	override var lastName: String,

	@Email
	@NotNull
	@Size(max = 256)
	var email: String?, // non unique for compat reasons atm

	/**
	 * Roles granted to the user.
	 * An empty roles set is equivalent to the [Role.USER] role.
	 */
	@JdbcTypeCode(SqlTypes.ARRAY)
	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "text[]") // Unfortunately explicit, as IJ is a bit confused otherwise
	var roles: Set<Role> = emptySet(),

	// TODO: `permissions`? `revoked: Set<Permission>`?
) : AbstractEntity(),
	User
