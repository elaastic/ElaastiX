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

package org.elaastix.engine.mvc

import jakarta.servlet.http.HttpServletRequest
import org.elaastix.activityframework.annotations.ElaastixActivity
import org.elaastix.activityframework.annotations.PlayerProcedure
import org.elaastix.activityframework.annotations.PlayerQuery
import org.elaastix.commons.spring.findMergedAnnotation
import org.elaastix.commons.spring.findMergedAnnotationOnClass
import org.elaastix.engine.PLAYER_RPC_ENTRYPOINT
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.getBeansWithAnnotation
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Configuration
import org.springframework.web.accept.ApiVersionResolver
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.config.annotation.ApiVersionConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.lang.reflect.Method
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.jvmName

/**
 * Configuration bean registering all [ElaastixActivity] beans and their RPC endpoints.
 *
 * TODO: this is most likely not the impl we want.
 *       it's convenient atm because it's easy to implement and picked up by springdoc nicely
 */
@Configuration
class PlayerRoutesRegistration(
	private val context: ApplicationContext,
	// https://github.com/spring-projects/spring-boot/issues/31961#issuecomment-1202893925
	@Qualifier("requestMappingHandlerMapping")
	private val mapping: RequestMappingHandlerMapping,
) {
	companion object {
		private val LOGGER = LoggerFactory.getLogger(PlayerRoutesRegistration::class.java)
	}

	// Miscellaneous references:
	// https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-requestmapping.html#mvc-ann-requestmapping-registration
	//
	// NOTE: Do not be smarter than Spring's own annotation helpers. We actively want to team play with the framework.
	// Specifically Spring has tons of helpers like `@AliasFor` which we do want to promote the usage when relevant.
	// Plain Kotlin reflection would be good enough for 99% of cases, and infuriating in that 1%.
	// Let's team up with the framework instead of fighting it, the extra % is worth it :)

	/** Configuration procedure called by Spring. */
	@Autowired // IJ is wrong, autowire is needed so Spring calls this
	@Suppress("SpringJavaInjectionPointsAutowiringInspection")
	fun registerMvcMappings() {
		LOGGER.debug("Registering Elaastix controllers")
		for ((bean, controller) in context.getBeansWithAnnotation<ElaastixActivity>()) {
			// SAFETY: If we get here and there's not an ElaastixActivity annotation, what the hell???
			val annotation: ElaastixActivity = controller.findMergedAnnotationOnClass()!!

			LOGGER.debug("Registering ${controller::class.jvmName} on ${annotation.authority}")
			for (method in controller::class.declaredMemberFunctions) {
				processMapping(
					bean,
					annotation.authority,
					method.javaMethod!!, // SAFETY: Comes from [declaredMemberFunctions]
				)
			}
		}

		LOGGER.debug("DONE: Registering Elaastix controllers")
	}

	private final fun processMapping(bean: String, authority: String, handler: Method) {
		handler.findMergedAnnotation<PlayerQuery>()?.let {
			registerRpcMapping(RequestMethod.GET, bean, authority, handler)
			return
		}

		handler.findMergedAnnotation<PlayerProcedure>()?.let {
			registerRpcMapping(RequestMethod.POST, bean, authority, handler)
			return
		}
	}

	private final fun registerRpcMapping(
		method: RequestMethod,
		bean: String,
		authority: String,
		handler: Method,
	) {
		val path = "$authority.${handler.name}"
		LOGGER.debug("Registering Player RPC {} {}", method, path)
		mapping.registerMapping(
			RequestMappingInfo
				.paths("$PLAYER_RPC_ENTRYPOINT/$path")
				.options(mapping.builderConfiguration)
				.methods(method)
				.build(),
			bean,
			handler,
		)
	}

	/** Configurer setting a dummy version resolver to disable versioning on `/rpc`. */
	@Configuration
	class RpcVersioningConfiguration : WebMvcConfigurer {
		private object RpcDummyVersionResolver : ApiVersionResolver {
			override fun resolveVersion(request: HttpServletRequest): String? =
				if (request.servletPath.startsWith(PLAYER_RPC_ENTRYPOINT)) "v0" else null
		}

		override fun configureApiVersioning(configurer: ApiVersionConfigurer) {
			configurer.useVersionResolver(RpcDummyVersionResolver)
		}
	}
}
