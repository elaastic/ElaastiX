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

package org.elaastix.server.tmp

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.elaastix.server.users.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.security.web.util.matcher.AnyRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.stereotype.Component
import java.util.UUID

@Configuration
@EnableWebSecurity
class WebSecurityConfig(
    val authProvider: CustomAuthenticationProvider,
    val authConfig: AuthenticationConfiguration,
) {
    @Bean
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
                CustomAuthenticationProcessingFilter(
                    AnyRequestMatcher.INSTANCE,
                    authConfig.authenticationManager,
                )
            )

            authorizeHttpRequests {
                authorize(anyRequest, authenticated)
            }
        }

        return http.build()
    }

    class CustomAuthenticationProcessingFilter(
        requestMatcher: RequestMatcher,
        authenticationManager: AuthenticationManager,
    ) : AbstractAuthenticationProcessingFilter(requestMatcher) {
        init {
            setAuthenticationManager(authenticationManager)
        }

        @Suppress("ReturnCount")
        override fun attemptAuthentication(
            request: HttpServletRequest,
            response: HttpServletResponse
        ): Authentication? {
            return runCatching {
                request.getHeader("Authorization")?.let(UUID::fromString)
            }.getOrNull()?.let {
                authenticationManager.authenticate(
                    PreAuthenticatedAuthenticationToken(it, null)
                )
            }
        }

        protected override fun successfulAuthentication(
            request: HttpServletRequest,
            response: HttpServletResponse,
            chain: FilterChain,
            authResult: Authentication
        ) {
            SecurityContextHolder.getContext().authentication = authResult
            chain.doFilter(request, response)
        }

        protected override fun unsuccessfulAuthentication(
            request: HttpServletRequest,
            response: HttpServletResponse,
            exception: AuthenticationException
        ) {
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.writer.print(
                JsonObject(
                    mapOf(
                        "error" to JsonPrimitive("Unauthenticated"),
                        "message" to JsonPrimitive(exception.message)
                    )
                )
            )
            response.flushBuffer()
        }
    }

    @Component
    class CustomAuthenticationProvider(val userRepository: UserRepository) : AuthenticationProvider {
        override fun authenticate(authentication: Authentication): Authentication? {
            val customToken = authentication.principal as? UUID ?: throw object : AuthenticationException(null) {}
            val user = userRepository.findByIdOrNull(customToken) ?: return null

            return UsernamePasswordAuthenticationToken(
                user,
                null,
                emptyList<GrantedAuthority>()
            )
        }

        override fun supports(authentication: Class<*>): Boolean {
            return PreAuthenticatedAuthenticationToken::class.java == authentication
        }
    }
}
