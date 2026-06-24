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

package org.elaastix.server.authn.tmp.lti

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.elaastix.commons.platform.debt.SciconumTechDebt
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@SciconumTechDebt
@RestController
@ConditionalOnLti
@RequestMapping("/authn/tmp/lti")
class LtiLaunchController(private val ltiService: LtiService) {
	@PostMapping("/launch", "/elaastic-questions/launch")
	fun launch(@RequestBody data: LtiLaunchDataDto, request: HttpServletRequest, response: HttpServletResponse) =
		ltiService.launch(data, request, response)

	@PostMapping("/finalise-consent")
	fun finalise(request: HttpServletRequest, response: HttpServletResponse) = ltiService.receiveConsent(request, response)
}
