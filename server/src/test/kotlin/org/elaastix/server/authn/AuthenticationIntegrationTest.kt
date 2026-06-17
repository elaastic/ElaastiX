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

package org.elaastix.server.authn

import org.elaastix.commons.data.UuidSerializer.toStringBase36
import org.elaastix.server.users.entities.UserEntity
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import testutils.IntegrationTest
import testutils.MockUser
import testutils.WithMockUser

@SpringBootTest
class AuthenticationIntegrationTest : IntegrationTest() {
	@Test
	fun `accessing an endpoint without authenticating yields 401`() {
		// TODO: improve :)
		mvc.get("/")
			.andExpect { status { isUnauthorized() } }
			.andExpect { content { contentType(MediaType.APPLICATION_PROBLEM_JSON) } }
	}

	@Test
	fun `accessing an endpoint with an invalid authentication token yields 401`() {
		// TODO: improve :)
		mvc.get("/") {
			headers {
				set("Authorization", "pls gib access trust")
			}
		}
			.andExpect { status { isUnauthorized() } }
			.andExpect { content { contentType(MediaType.APPLICATION_PROBLEM_JSON) } }
	}

	@Test
	@WithMockUser(persist = false) // This is more of a "test harness test" than a test on its own
	fun `works when authenticated via Spring Security mock helper`(@MockUser user: UserEntity) {
		mvc.get("/v1/authn/tmp/who-am-i")
			.andExpect { status { isOk() } }
			.andExpect { content { string(user.id.toStringBase36()) } }
	}

	@SpringBootTest(properties = ["elaastix.security.authn.develop=false"])
	class NormalMode : IntegrationTest() {
		@Test
		fun `accessing an endpoint with develop authentication is permitted`() {
			val user = UserEntity(
				firstName = "Ada",
				lastName = "Lovelace",
				email = "ada.lovelace@elaastix.test",
			)

			user.persist()
			mvc.get("/v1/authn/tmp/who-am-i") {
				headers { set("Authorization", "Develop ${user.id}") }
			}
				.andExpect { status { isUnauthorized() } }
				.andExpect { content { contentType(MediaType.APPLICATION_PROBLEM_JSON) } }
		}
	}

	@SpringBootTest(properties = ["elaastix.security.authn.develop=true"])
	class DevelopMode : IntegrationTest() {
		@Test
		fun `accessing an endpoint with develop authentication is permitted`() {
			val user = UserEntity(
				firstName = "Ada",
				lastName = "Lovelace",
				email = "ada.lovelace@elaastix.test",
			)

			user.persist()
			mvc.get("/v1/authn/tmp/who-am-i") {
				headers { set("Authorization", "Develop ${user.id}") }
			}
				.andExpect { status { isOk() } }
		}
	}
}
