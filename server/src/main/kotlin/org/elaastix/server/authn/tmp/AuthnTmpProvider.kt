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

package org.elaastix.server.authn.tmp

import org.elaastix.server.users.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.stereotype.Component
import kotlin.uuid.Uuid

@Component
class AuthnTmpProvider(val userRepository: UserRepository) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication): Authentication? {
        val user = (authentication.principal as? Uuid)?.let(userRepository::findByIdOrNull)
            ?: throw BadCredentialsException("Bad credentials")

        return UsernamePasswordAuthenticationToken(
            user,
            null,
            emptyList<GrantedAuthority>(),
        )
    }

    override fun supports(authentication: Class<*>): Boolean =
        PreAuthenticatedAuthenticationToken::class.java == authentication
}
