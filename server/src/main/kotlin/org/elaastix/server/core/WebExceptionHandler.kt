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

package org.elaastix.server.core

import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.ErrorResponse
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

/**
 * Controller Advice responsible for error handling.
 *
 * Only existing exceptions should be handled here.
 * New exceptions should extend [org.springframework.web.ErrorResponseException] instead.
 */
@RestControllerAdvice
class WebExceptionHandler : ResponseEntityExceptionHandler() {
	@ExceptionHandler(NotImplementedError::class)
	fun handleNotImplemented(ex: NotImplementedError) =
		ErrorResponse.create(ex, HttpStatus.NOT_IMPLEMENTED, "This feature is not implemented.")

	@ExceptionHandler(AuthenticationException::class)
	fun handleAuthenticationException(ex: AuthenticationException) =
		when (ex) {
			is AuthenticationServiceException ->
				ErrorResponse.create(ex, HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while authenticating.")

			else ->
				ErrorResponse.create(ex, HttpStatus.UNAUTHORIZED, "Authentication required.")
		}
}
