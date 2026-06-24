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

import jakarta.servlet.http.HttpServletRequest
import org.elaastix.commons.data.Uuid
import org.elaastix.commons.platform.debt.SciconumTechDebt
import org.springframework.security.web.authentication.AuthenticationConverter
import org.springframework.stereotype.Component

/**
 * Converter responsible for handling the initial token handling routine.
 * Extracts the `Authorization` header and parses it into the appropriate token type.
 */
@Component
class ElaastixAuthenticationConverter @SciconumTechDebt constructor(
	private val uuidAuthnCookieService: UuidAuthnCookieService,
) : AuthenticationConverter {
	companion object {
		private const val AUTHORISATION_HEADER = "Authorization"
		private const val DEVELOP_TOKEN_TYPE = "Develop"
		private const val JWT_TOKEN_TYPE = "JWT"
	}

	@OptIn(SciconumTechDebt::class)
	override fun convert(request: HttpServletRequest) = convertHeaderAuthn(request) ?: convertCookieAuthn(request)

	@Suppress("NestedBlockDepth")
	private fun convertHeaderAuthn(request: HttpServletRequest) =
		request.getHeader(AUTHORISATION_HEADER)?.let { authorisation ->
			val parts = authorisation.split(' ')
			when (parts.size) {
				2 -> Uuid.parseOrNull(parts[1])?.let {
					when (parts[0]) {
						DEVELOP_TOKEN_TYPE -> DevelopAuthenticationToken(it)
						JWT_TOKEN_TYPE -> TODO("Not implemented")
						else -> null
					}
				}

				else -> null
			}
		}

	@SciconumTechDebt
	private fun convertCookieAuthn(request: HttpServletRequest) =
		uuidAuthnCookieService.readCookie(request)?.let { (cookie, uuid) -> CookieAuthenticationToken(cookie, uuid) }
}
