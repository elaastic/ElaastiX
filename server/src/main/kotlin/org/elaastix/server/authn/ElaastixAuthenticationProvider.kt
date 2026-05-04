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

import org.elaastix.server.authn.DevelopAuthenticationToken
import org.elaastix.server.users.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.InsufficientAuthenticationException
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import javax.security.auth.login.AccountNotFoundException

/**
 * Authentication provider for Elaastix.
 * Finalises the authentication of requests which had a token found by [ElaastixAuthenticationConverter]
 */
@Component
class ElaastixAuthenticationProvider(
	@Value("#{environment.acceptsProfiles('develop')}")
	private val isDevelop: Boolean,
	private val userRepository: UserRepository,
) : AuthenticationProvider {
	override fun authenticate(authentication: Authentication) =
		when (authentication) {
			is DevelopAuthenticationToken -> authenticateDevelop(authentication)
			else -> null // COVERAGE: unreachable
		}

	private fun authenticateDevelop(authentication: DevelopAuthenticationToken): Authentication {
		if (!isDevelop) throw InsufficientAuthenticationException("Use of this type of token is not permitted.")

		val user = userRepository.findByIdOrNull(authentication.uuid)
			?: throw AccountNotFoundException("User does not exist.")

		return ElaastixAuthentication(user, authentication, true)
	}

	override fun supports(authentication: Class<*>) =
		when (authentication) {
			DevelopAuthenticationToken::class.java -> true
			else -> false
		}
}
