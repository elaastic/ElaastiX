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
import org.elaastix.server.prefersCbor
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.web.accept.ContentNegotiationManager
import org.springframework.web.context.request.ServletWebRequest

/**
 * Request filter responsible for the authentication of users using the `Authorization` header.
 */
class ElaastixAuthenticationFilter(
	requestMatcher: RequestMatcher,
	authnManager: AuthenticationManager,
	private val contentNegotiationManager: ContentNegotiationManager,
) : AbstractAuthenticationProcessingFilter(requestMatcher, authnManager) {
	init {
		setAuthenticationConverter(ElaastixAuthenticationConverter)
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
		request: HttpServletRequest,
		response: HttpServletResponse,
		failed: AuthenticationException,
	) {
		response.status = HttpServletResponse.SC_UNAUTHORIZED
		val mediaTypes = contentNegotiationManager.resolveMediaTypes(ServletWebRequest(request))

		if (mediaTypes.prefersCbor()) {
			response.contentType = "application/cbor"
			TODO("CBOR support not implemented")
		} else {
			response.contentType = "application/json"
			response.writer.write(("{\"error\": \"Authentication required\", \"message\": \"" + failed.message) + "\"}")
		}
	}
}
