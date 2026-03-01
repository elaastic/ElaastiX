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

package org.elaastix.server.infrastructure.seed

import org.elaastix.server.users.UserRepository
import org.elaastix.server.users.entities.User
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.data.domain.Example
import org.springframework.data.domain.ExampleMatcher
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

/**
 * Component responsible for populating, in development, the database with initial data.
 * Does not run during unit and integration tests. Individual tests are responsible for setting up their data; relying
 * on data created outside the scope of the tests is a bad practice.
 */
@Component
@Profile("develop & !testing")
class DatabaseSeeder(private val userRepository: UserRepository) : ApplicationRunner {
    override fun run(args: ApplicationArguments) {
        doInitUsers()
    }

    private fun doInitUsers() {
        if (userRepository.count() == 0L) {
            userRepository.persist(User())
            userRepository.persist(User())
            userRepository.persist(User())
        }

        // Gross workaround the lack of findAll.
        userRepository.findAll(
            Example.of(
                User(),
                ExampleMatcher.matchingAny(),
            ),
            @Suppress("MagicNumber")
            Pageable.ofSize(3),
        )
            .forEachIndexed { idx, user -> println("Test user ${idx + 1}: ${user.id}") }
    }
}
