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

package org.elaastix.activityframework.annotations

import org.springframework.core.annotation.AliasFor
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * Annotation for mapping procedure endpoints to a request handler.
 *
 * Unlike with traditional REST, the RPC-oriented approach does not distinguish between creation, update, and removal
 * at the HTTP layer. Thus, all procedure requests are HTTP POST.
 */
@ResponseStatus
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class PlayerProcedure(
	/**
	 * The name of the query. Must be alphanumeric.
	 * See [Namespaced Identifiers](https://atproto.com/specs/nsid).
	 *
	 * Defaults to the name of the handler function, and shouldn't need to be changed.
	 */
	val name: String = "",

	/**
	 * The outcome of a successful procedure call. Defaults to OK.
	 * TODO: Decouple from HTTP (ProcedureOutcome enum)
	 */
	@get:AliasFor(attribute = "code", annotation = ResponseStatus::class)
	val status: HttpStatus = HttpStatus.OK,
)
