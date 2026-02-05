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

package org.elaastix.server.bootstrap

import jakarta.annotation.PostConstruct
import org.elaastix.server.users.UserRepository
import org.elaastix.server.users.entities.User
import org.springframework.context.annotation.Profile
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
@Profile("develop")
class DatabaseSeeder(val userRepository: UserRepository) {
    @PostConstruct
    fun doInitUsers() {
        if (userRepository.count() == 0L) {
            userRepository.save(User())
            userRepository.save(User())
            userRepository.save(User())
        }

        @Suppress("MagicNumber")
        userRepository.findAll(Pageable.ofSize(3)).forEachIndexed { idx, user -> println("Test user $idx: ${user.id}") }
    }
}
