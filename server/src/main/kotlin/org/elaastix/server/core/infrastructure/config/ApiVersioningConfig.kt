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

package org.elaastix.server.core.infrastructure.config

import jakarta.servlet.http.HttpServletRequest
import org.elaastix.server.ElaastixServerApplication
import org.springframework.context.annotation.Configuration
import org.springframework.http.server.RequestPath
import org.springframework.web.accept.ApiVersionResolver
import org.springframework.web.accept.StandardApiVersionDeprecationHandler
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.method.HandlerTypePredicate
import org.springframework.web.servlet.config.annotation.ApiVersionConfigurer
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.util.function.Predicate

/**
 * Configuration class for API Versioning.
 * See https://docs.spring.io/spring-framework/reference/web/webmvc-versioning.html
 */
@Configuration
class ApiVersioningConfig : WebMvcConfigurer {
	private companion object {
		val pathVersioningPredicate: Predicate<RequestPath> = { !it.toString().startsWith("/documentation") }

		val handlerVersioningPredicate: Predicate<Class<*>> =
			HandlerTypePredicate.forBasePackageClass(ElaastixServerApplication::class.java)
				.and(HandlerTypePredicate.forAnnotation(RestController::class.java))

		// Needed so Spring doesn't complain about missing API version.
		object ScalarDummyVersionResolver : ApiVersionResolver {
			override fun resolveVersion(request: HttpServletRequest): String? =
				if (request.servletPath.startsWith("/documentation")) "v0" else null
		}
	}

	override fun configurePathMatch(configurer: PathMatchConfigurer) {
		configurer.addPathPrefix("/v{apiVersion}", handlerVersioningPredicate)
	}

	override fun configureApiVersioning(configurer: ApiVersionConfigurer) {
		configurer
			.usePathSegment(0, pathVersioningPredicate)
			.useVersionResolver(ScalarDummyVersionResolver)
			.setDeprecationHandler(StandardApiVersionDeprecationHandler())
	}
}
