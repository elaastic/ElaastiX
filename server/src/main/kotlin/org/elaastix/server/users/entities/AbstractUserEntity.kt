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

import jakarta.persistence.DiscriminatorColumn
import jakarta.persistence.Entity
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.Table
import org.elaastix.commons.jpa.AbstractEntity
import org.elaastix.mm.users.User

/**
 * Abstract entity for users.
 * Defines the JPA inheritance strategy used for specialised entities.
 *
 * @see [User]
 */
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "userType", length = 16)
abstract class AbstractUserEntity :
    AbstractEntity(),
    User
