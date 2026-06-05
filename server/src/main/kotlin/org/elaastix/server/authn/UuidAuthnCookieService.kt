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

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import org.elaastix.commons.data.Uuid
import org.elaastix.commons.platform.debt.SciconumTechDebt
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.encrypt.BytesEncryptor
import org.springframework.stereotype.Service
import kotlin.io.encoding.Base64
import kotlin.time.Duration.Companion.days
import kotlin.time.DurationUnit

@Service
@SciconumTechDebt
class UuidAuthnCookieService(
	@Value($$"${elaastix.cookie-secure:true}")
	private val cookieSecure: Boolean,
	private val encryptor: BytesEncryptor,
) {
	companion object {
		private const val COOKIE_UUID_NAME = "authn-uuid"
	}

	fun createCookie(uuid: Uuid) =
		Cookie(COOKIE_UUID_NAME, Base64.encode(encryptor.encrypt(uuid.toByteArray()))).apply {
			secure = cookieSecure
			isHttpOnly = true
			maxAge = 1.0.days.toInt(DurationUnit.SECONDS)
		}

	fun readCookie(request: HttpServletRequest) =
		request.cookies.find { it.name == COOKIE_UUID_NAME }
			?.runCatching { this to Uuid.fromByteArray(encryptor.decrypt(Base64.decode(value))) }
			?.getOrNull()
}
