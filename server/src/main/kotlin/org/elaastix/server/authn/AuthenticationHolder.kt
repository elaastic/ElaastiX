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

package org.elaastix.server.authn

import org.elaastix.server.users.entities.User
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException

/**
 * Facade exposing the authentication status for the current request context.
 *
 * For correctness, [authenticatedUser] is specified as nullable, as there is no guarantee authentication succeeded.
 * Only after checking for whether the user is non-null can we safely assert that the user is indeed authenticated.
 *
 * A convenience helper [`User?.required`][required] is provided to get a non-nullable [User] without having to deal
 * with null checking everytime.
 */
@Component
class AuthenticationHolder {
    /**
     * Whether the current context holds a valid authentication.
     */
    val isAuthenticated: Boolean
        get() = SecurityContextHolder.getContext().authentication?.isAuthenticated == true

    /**
     * Authenticated user for the current context. `null` if unauthenticated.
     */
    val authenticatedUser: User?
        get() = SecurityContextHolder.getContext().authentication?.principal as? User
}

/**
 * Convenience helper for getting a non-null [User] reference, aborting the HTTP request with status code 401 otherwise.
 */
fun User?.required(): User = this ?: throw HttpClientErrorException(HttpStatus.UNAUTHORIZED)
