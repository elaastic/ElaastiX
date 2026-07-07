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

import jakarta.servlet.http.HttpServletResponse
import org.elaastix.commons.platform.debt.SciconumTechDebt
import org.elaastix.server.authn.UuidAuthnCookieService
import org.elaastix.server.users.entities.UserEntity
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@SciconumTechDebt
@Profile("develop")
@RestController
@RequestMapping("/authn/tmp", version = "1+")
class AuthConverter(private val uuidAuthnCookieService: UuidAuthnCookieService) {

	@PostMapping("/convert")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	fun convertAuthn(@AuthenticationPrincipal user: UserEntity, response: HttpServletResponse) {
		response.addCookie(uuidAuthnCookieService.createCookie(user.id))
	}

	@DeleteMapping("/logout")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	fun logout(@AuthenticationPrincipal user: UserEntity, response: HttpServletResponse) {
		response.addCookie(uuidAuthnCookieService.deleteCookie(user.id))
	}
}
