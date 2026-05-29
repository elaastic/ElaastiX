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

import org.junit.jupiter.api.Test
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import testutils.IntegrationTest
import testutils.WithMockUser

@WithMockUser
@Import(WebExceptionHandlerIntegrationTest.TestController::class)
class WebExceptionHandlerIntegrationTest : IntegrationTest() {
	@Test
	fun `NotFoundException is handled by exception handler`() {
		mvc.get("/v-1/does-not-exist")
			.andExpect { status { isNotFound() } }
			.andExpect { content { contentType(MediaType.APPLICATION_PROBLEM_JSON) } }
	}

	@Test
	fun `NotImplementedError is handled by exception handler`() {
		mvc.get("/v-1/not-implemented")
			.andExpect { status { isNotImplemented() } }
			.andExpect { content { contentType(MediaType.APPLICATION_PROBLEM_JSON) } }
	}

	@RestController
	class TestController {
		@GetMapping("/not-implemented", version = "-1")
		fun notImplemented(): Nothing = throw NotImplementedError()
	}
}
