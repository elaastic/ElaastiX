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

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerExceptionResolver

/**
 * Request filter responsible for the authentication of users using the `Authorization` header.
 */
class ElaastixAuthenticationFilter(
	requestMatcher: RequestMatcher,
	authnManager: AuthenticationManager,
	authenticationConverter: ElaastixAuthenticationConverter,
	private val handlerExceptionResolver: HandlerExceptionResolver,
) : AbstractAuthenticationProcessingFilter(requestMatcher, authnManager) {
	init {
		setAuthenticationConverter(authenticationConverter)
	}

	override fun successfulAuthentication(
		request: HttpServletRequest,
		response: HttpServletResponse,
		chain: FilterChain,
		authResult: Authentication,
	) {
		val context = SecurityContextHolder.createEmptyContext().apply { authentication = authResult }
		SecurityContextHolder.setContext(context)
		chain.doFilter(request, response)
	}

	override fun unsuccessfulAuthentication(
		req: HttpServletRequest,
		res: HttpServletResponse,
		ex: AuthenticationException,
	) {
		handlerExceptionResolver.resolveException(req, res, null, ex)
	}

	/**
	 * Factory receiving autowired properties from Spring and constructing instances of [ElaastixAuthenticationFilter].
	 */
	@Component
	class ElaastixAuthenticationFilterFactory(
		private val handlerExceptionResolver: HandlerExceptionResolver,
		private val authenticationConverter: ElaastixAuthenticationConverter,
	) {
		/** The factory method. */
		fun createFilter(requestMatcher: RequestMatcher, authnManager: AuthenticationManager): ElaastixAuthenticationFilter =
			ElaastixAuthenticationFilter(
				requestMatcher,
				authnManager,
				authenticationConverter,
				handlerExceptionResolver,
			)
	}
}
