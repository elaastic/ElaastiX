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
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.encrypt.Encryptors
import org.springframework.security.crypto.password.DelegatingPasswordEncoder
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter
import org.springframework.security.web.util.matcher.AnyRequestMatcher
import org.springframework.util.DigestUtils
import org.springframework.web.servlet.HandlerExceptionResolver
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.security.SecureRandom

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
		authnProvider: ElaastixAuthenticationProvider,
		authnConfig: AuthenticationConfiguration,
		authnFilterFactory: ElaastixAuthenticationFilter.ElaastixAuthenticationFilterFactory,
		handlerExceptionResolver: HandlerExceptionResolver,
	): SecurityFilterChain {
		http.authenticationProvider(authnProvider)
		http {
			cors { /* no-op */ }
			csrf { disable() }
			formLogin { disable() }
			logout { disable() }
			httpBasic { disable() }
			sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }

			addFilterBefore<AnonymousAuthenticationFilter>(
				authnFilterFactory.createFilter(
					AnyRequestMatcher.INSTANCE,
					authnConfig.authenticationManager,
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

	/**
	 * Randomness is provided by a cryptographically secure pseudo-random number generator to guarantee the highest
	 * degree of fairness across rolls, as a CSPRNG is guaranteed to be as unpredictable as possible.
	 */
	@Bean
	fun random() = SecureRandom()

	/**
	 * Password encoding uses Spring's [DelegatingPasswordEncoder], which allows us to update the password encoding
	 * scheme in the future while maintaining full backwards compatibility.
	 *
	 * Argon2 (RFC 9106) is the current state-of-the-art for password hashing. While it is very unlikely Argon2 will
	 * be replaced by another algorithm, its parameters are designed to evolve over time to maintain optimal security
	 * as computing capacity increases.
	 */
	@Bean
	fun passwordEncoder() =
		DelegatingPasswordEncoder(
			"argon2+v5_8",
			mapOf(
				"argon2+v5_8" to Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8(),
			),
		)

	@Bean
	@ConditionalOnProperty(name = ["elaastix.security.encryption-key"], matchIfMissing = false)
	fun encryptors(@Value($$"${elaastix.security.encryption-key}") key: String) =
		// SAFETY: MUST be AEAD. `stronger` uses AES-GCM which is acceptable.
		// https://bsky.app/profile/joncallas.bsky.social/post/3jz3qztg3du2y
		Encryptors.stronger(key, DigestUtils.md5Digest(key.toByteArray()).toHexString())
}
