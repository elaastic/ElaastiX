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

package org.elaastix.server.infrastructure.config

import org.elaastix.server.authn.ElaastixAuthenticationFilter
import org.elaastix.server.authn.ElaastixAuthenticationProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.util.matcher.AnyRequestMatcher
import org.springframework.web.accept.ContentNegotiationManager

/**
 * Configuration class for Spring Security.
 * See https://docs.spring.io/spring-security/reference/servlet/integrations/mvc.html
 */
@Configuration
@EnableWebSecurity
class WebSecurityConfig(
	private val authProvider: ElaastixAuthenticationProvider,
	private val authConfig: AuthenticationConfiguration,
	private val contentNegotiationManager: ContentNegotiationManager,
) {
	@Bean
	@Suppress("UndocumentedPublicFunction")
	fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
		http.authenticationProvider(authProvider)
		http {
			cors { }
			csrf { disable() }
			formLogin { disable() }
			logout { disable() }
			httpBasic { disable() }
			sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }

			addFilterBefore<AnonymousAuthenticationFilter>(
				ElaastixAuthenticationFilter(
					AnyRequestMatcher.INSTANCE,
					authConfig.authenticationManager,
					contentNegotiationManager,
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
				authenticationEntryPoint = HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
			}
		}

		return http.build()
	}
}
