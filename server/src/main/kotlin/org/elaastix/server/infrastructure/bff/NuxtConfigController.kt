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

package org.elaastix.server.infrastructure.bff

import org.elaastix.server.infrastructure.bff.dtos.NuxtContextDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Endpoints responsible for providing the first-party Nuxt application with its configuration information and initial
 * context.
 *
 * As this is a Backend-for-Frontend, it is inherently tightly coupled with the frontend it serves and therefore
 * versioned separately than the rest of the API. All endpoints will work the same regardless of the selected API
 * version.
 *
 * **Caution**: as the `internal` prefix suggests, this endpoint is **not intended for public use**. The endpoints and
 * their behaviour may change without notice, including changes that are not backwards compatible.
 */
@RestController
@RequestMapping("/internal/nuxt", version = "0+")
class NuxtConfigController {
    /**
     * Returns context and configuration information needed for the first-party Nuxt webapp.
     */
    @GetMapping("/context-v1")
    fun getContextVersion1(): NuxtContextDto {
        // TODO: pull user from the AuthenticationFacade, pull feature flags config, build a DTO out of it...
        //       this is mostly just scaffolding for later
        return NuxtContextDto(emptyList(), null)
    }
}
