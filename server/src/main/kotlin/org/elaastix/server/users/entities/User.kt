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

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version
import org.elaastix.server.UUID_Z
import org.hibernate.annotations.UuidGenerator
import org.hibernate.id.uuid.UuidVersion7Strategy
import java.util.UUID
import org.elaastix.mm.user.User as MMUser

@Entity
// Hibernate just casually broke `globally_quoted_identifiers` and doesn't want to fix it. https://hibernate.atlassian.net/browse/HHH-19973
@Table(name = "\"user\"")
class User : MMUser {
    @Id
    @UuidGenerator(algorithm = UuidVersion7Strategy::class)
    var id: UUID = UUID_Z

    @Version
    var version = 0L
}
