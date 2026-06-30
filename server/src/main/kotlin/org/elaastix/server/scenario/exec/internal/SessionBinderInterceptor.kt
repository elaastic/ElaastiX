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

package org.elaastix.server.scenario.exec.internal

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.elaastix.commons.data.Uuid
import org.elaastix.commons.data.UuidSerializer.tryFromBase36
import org.elaastix.commons.exceptions.BadRequestException
import org.elaastix.commons.platform.debt.SciconumTechDebt
import org.elaastix.server.authn.ElaastixAuthentication
import org.elaastix.server.scenario.exec.repositories.SciconumLearnerSessionRepository
import org.springframework.web.servlet.HandlerInterceptor

/**
 * Interceptor binding the active scenario and learner session to the request.
 */
@SciconumTechDebt
class SessionBinderInterceptor(private val learnerSessionRepository: SciconumLearnerSessionRepository) :
	HandlerInterceptor {
	companion object {
		/** Request attribute name storing the scenario session entity. */
		const val ATTR_SCENARIO_SESSION = "org.elaastix.engine.session.scenario"

		/** Request attribute name storing the learner session entity. */
		const val ATTR_LEARNER_SESSION = "org.elaastix.engine.session.learner"
	}

	override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
		val nsid = request.requestURI.split('/').last()

		// Engine and platform are special purpose
		if (nsid.startsWith("org.elaastix.engine") || nsid.startsWith("org.elaastix.platform")) return true

		val sessionId = request.getParameter("sessionId")?.let { Uuid.tryFromBase36(it) }?.getOrNull()
			?: throw BadRequestException("Missing or invalid `sessionId`")

		val auth = request.userPrincipal as? ElaastixAuthentication
			?: error("Unexpected or missing request authentication; should've been processed by Spring Security?")

		val session = learnerSessionRepository.findByIdAndLearner(sessionId, auth.principal)
			?: throw BadRequestException("Invalid session")

		request.setAttribute(ATTR_LEARNER_SESSION, session)
		request.setAttribute(ATTR_SCENARIO_SESSION, session.scenarioSession)

		return true
	}
}
