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

import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.web.authentication.AuthenticationConverter
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import kotlin.uuid.Uuid

class TmpAuthnConverter : AuthenticationConverter {
    override fun convert(request: HttpServletRequest) = request.getHeader("Authorization")
        ?.let(Uuid::parseOrNull)
        ?.let { PreAuthenticatedAuthenticationToken(it, null) }
}
