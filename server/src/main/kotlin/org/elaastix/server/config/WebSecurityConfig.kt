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

package org.elaastix.server.config

import org.elaastix.server.authn.tmp.AuthnTmpProcessingFilter
import org.elaastix.server.authn.tmp.AuthnTmpProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter
import org.springframework.security.web.util.matcher.AnyRequestMatcher

@Configuration
@EnableWebSecurity
class WebSecurityConfig(
    private val authProvider: AuthnTmpProvider,
    private val authConfig: AuthenticationConfiguration,
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
                AuthnTmpProcessingFilter(
                    AnyRequestMatcher.INSTANCE,
                    authConfig.authenticationManager,
                ),
            )

            authorizeHttpRequests {
                authorize(anyRequest, authenticated)
            }
        }

        return http.build()
    }
}
