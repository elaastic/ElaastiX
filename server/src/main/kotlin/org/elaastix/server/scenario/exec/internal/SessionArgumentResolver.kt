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

import org.elaastix.commons.platform.debt.SciconumTechDebt
import org.elaastix.server.scenario.exec.annotation.LearnerSession
import org.elaastix.server.scenario.exec.annotation.ScenarioSession
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

/**
 * Spring argument resolver injecting [ScenarioSession] and [LearnerSession].
 */
class SessionArgumentResolver : HandlerMethodArgumentResolver {
	override fun supportsParameter(parameter: MethodParameter) = parameter.isSession()

	@OptIn(SciconumTechDebt::class)
	override fun resolveArgument(
		parameter: MethodParameter,
		mavContainer: ModelAndViewContainer?,
		webRequest: NativeWebRequest,
		binderFactory: WebDataBinderFactory?,
	) = when {
		parameter.isScenarioSession() ->
			webRequest.getAttribute(SessionBinderInterceptor.ATTR_SCENARIO_SESSION, RequestAttributes.SCOPE_REQUEST)

		parameter.isLearnerSession() ->
			webRequest.getAttribute(SessionBinderInterceptor.ATTR_LEARNER_SESSION, RequestAttributes.SCOPE_REQUEST)

		else -> null
	}

	private fun MethodParameter.isSession() = isScenarioSession() || isLearnerSession()
	private fun MethodParameter.isScenarioSession() = hasParameterAnnotation(ScenarioSession::class.java)
	private fun MethodParameter.isLearnerSession() = hasParameterAnnotation(LearnerSession::class.java)
}
