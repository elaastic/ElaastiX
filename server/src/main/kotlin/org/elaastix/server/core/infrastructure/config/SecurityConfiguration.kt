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

import org.elaastix.server.authn.ElaastixAuthenticationFilter
import org.elaastix.server.authn.ElaastixAuthenticationProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter
import org.springframework.security.web.util.matcher.AnyRequestMatcher
import org.springframework.web.servlet.HandlerExceptionResolver
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Configuration for Spring Security features.
 *
 * See https://docs.spring.io/spring-security/reference/servlet/integrations/mvc.html
 * See https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfiguration : WebMvcConfigurer {
	override fun addCorsMappings(registry: CorsRegistry) {
		registry.addMapping("/openapi.json")
	}

	/**
	 * Security filter chain configuration.
	 *
	 * See https://docs.spring.io/spring-security/reference/servlet/configuration/kotlin.html
	 */
	@Bean
	fun securityFilterChain(
		http: HttpSecurity,
		authProvider: ElaastixAuthenticationProvider,
		authConfig: AuthenticationConfiguration,
		handlerExceptionResolver: HandlerExceptionResolver,
	): SecurityFilterChain {
		http.authenticationProvider(authProvider)
		http {
			cors { /* no-op */ }
			csrf { disable() }
			formLogin { disable() }
			logout { disable() }
			httpBasic { disable() }
			sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }

			addFilterBefore<AnonymousAuthenticationFilter>(
				ElaastixAuthenticationFilter(
					AnyRequestMatcher.INSTANCE,
					authConfig.authenticationManager,
					handlerExceptionResolver,
				),
			)

			authorizeHttpRequests {
				authorize("/v*/internal/nuxt/context-v1", permitAll)
				authorize("/actuator/**", permitAll)
				authorize("/openapi.json", permitAll)
				authorize("/documentation", permitAll)
				authorize("/documentation/**", permitAll)
				authorize(anyRequest, authenticated)
			}

			exceptionHandling {
				authenticationEntryPoint = { req, res, ex ->
					handlerExceptionResolver.resolveException(req, res, null, ex)
				}
			}
		}

		return http.build()
	}

	/**
	 * Spring Security extension for Spring Data.
	 * Gives access to `principal` in `@Query`, among other things.
	 *
	 * See https://docs.spring.io/spring-security/reference/servlet/integrations/data.html
	 */
	@Bean
	fun securityEvaluationContextExtension() = SecurityEvaluationContextExtension()
}
