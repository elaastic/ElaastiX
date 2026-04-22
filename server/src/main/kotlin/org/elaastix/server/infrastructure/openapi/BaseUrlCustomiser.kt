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

package org.elaastix.server.infrastructure.openapi

import org.springdoc.core.customizers.ServerBaseUrlCustomizer
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpRequest
import org.springframework.stereotype.Component

/**
 * Customiser adding `/api` prefix to the server base url.
 *
 * In development, when querying the Spring server directly the `/api` prefix is not added.
 * This allows using the Scalar documentation via `localhost:8080` without issues.
 */
@Component
class BaseUrlCustomiser(
    @Value("#{environment.acceptsProfiles('develop')}")
    private val isDevelop: Boolean,
    @Value($$"${server.port:8080}")
    private val serverPort: Int,
) : ServerBaseUrlCustomizer {
    override fun customize(serverBaseUrl: String, request: HttpRequest) =
        if (isDevelop && request.uri.host == "localhost" && request.uri.port == serverPort) {
            serverBaseUrl
        } else {
            "$serverBaseUrl/api"
        }
}
