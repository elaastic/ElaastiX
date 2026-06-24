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

package org.elaastix.server.authn.tmp.lti

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.transaction.Transactional
import kotlinx.serialization.json.Json
import net.oauth.OAuthAccessor
import net.oauth.OAuthConsumer
import net.oauth.OAuthException
import net.oauth.OAuthProblemException
import net.oauth.SimpleOAuthValidator
import org.apache.commons.logging.LogFactory
import org.elaastix.commons.data.Uuid
import org.elaastix.commons.data.UuidSerializer.fromBase36
import org.elaastix.commons.exceptions.BadRequestException
import org.elaastix.commons.platform.debt.SciconumTechDebt
import org.elaastix.server.assignments.participants.AssignmentParticipantsService
import org.elaastix.server.authn.UuidAuthnCookieService
import org.elaastix.server.authn.tmp.lti.lms.LmsUser
import org.elaastix.server.authn.tmp.lti.lms.LmsUserRepository
import org.elaastix.server.users.UserRepository
import org.elaastix.server.users.entities.UserEntity
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.encrypt.BytesEncryptor
import org.springframework.stereotype.Service
import kotlin.io.encoding.Base64

@Service
@ConditionalOnLti
@SciconumTechDebt
class LtiService(
	@Value($$"${elaastix.lti.consumer-key}")
	private val consumerKey: String,
	@Value($$"${elaastix.lti.consumer-secret}")
	private val consumerSecret: String,
	@Value($$"${elaastix.security.cookie-secure:true}")
	private val cookieSecure: Boolean,
	private val userRepository: UserRepository,
	private val lmsUserRepository: LmsUserRepository,
	private val assignmentParticipantsService: AssignmentParticipantsService,
	private val uuidAuthnCookieService: UuidAuthnCookieService,
	private val encryptor: BytesEncryptor,
	private val json: Json,
) {
	companion object {
		private val LOGGER = LogFactory.getLog(LtiService::class.java)

		private const val LTI_COOKIE = "lti-data"

		private const val NO_CONSUMER_KEY = "No consumer key provided in request params"
		private const val NO_CONSUMER = "No consumer corresponding to the provided key provided in request params"

		private const val PARAM_OAUTH_CONSUMER_KEY = "oauth_consumer_key"
		private const val DEFAULT_CALLBACK_URL = "about:blank"
	}

	@Transactional
	fun launch(dto: LtiLaunchDataDto, request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<Unit> {
		validateOAuthRequest(request)

		return when (val user = lmsUserRepository.findByLtiUserId(dto.user_id)) {
			null -> {
				// Cannot save user yet, consent to process data not acquired.
				// Save in an encrypted cookie for finalise. Safe because we use AEAD.
				val data = json.encodeToString(dto).toByteArray()
				val encrypted = Base64.encode(encryptor.encrypt(data))
				val cookie = Cookie("lti-data", encrypted).apply {
					secure = cookieSecure
					isHttpOnly = true
					maxAge = 300
				}

				response.addCookie(cookie)
				ResponseEntity.status(HttpStatus.FOUND).headers { it.add("Location", "/sciconum-consent") }.build()
			}

			else -> finalise(dto, user.user, response)
		}
	}

	@Transactional
	fun receiveConsent(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<Unit> {
		val cookie = request.cookies.find { it.name == LTI_COOKIE }
			?: throw BadRequestException()

		// Delete cookie
		response.addCookie(Cookie(LTI_COOKIE, "").apply { maxAge = 0 })

		val plain = encryptor.decrypt(Base64.decode(cookie.value)).decodeToString()
		val dto = json.decodeFromString<LtiLaunchDataDto>(plain)
		val user = userRepository.persist(
			UserEntity(
				firstName = dto.lis_person_name_given ?: "Anonymous",
				lastName = dto.lis_person_name_family ?: "User",
				email = dto.lis_person_contact_email_primary,
			),
		)

		lmsUserRepository.persist(LmsUser(dto.user_id, user))
		return finalise(dto, user, response)
	}

	private fun finalise(dto: LtiLaunchDataDto, user: UserEntity, response: HttpServletResponse): ResponseEntity<Unit> {
		response.addCookie(uuidAuthnCookieService.createCookie(user.id))
		assignmentParticipantsService.addParticipantToAssignmentById(
			Uuid.fromBase36(dto.resource_link_id),
			user.id,
		)

		return ResponseEntity.noContent().build()
	}

	@Suppress("all")
	private fun validateOAuthRequest(request: HttpServletRequest) {
		// https://github.com/elaastic/elaastic-questions-server/blob/29dc9a8a09e9063c0252007f61486f9d11a20eab/server/src/main/kotlin/org/elaastic/auth/lti/oauth/OauthService.kt
		val key = request.getParameter(PARAM_OAUTH_CONSUMER_KEY) ?: throw IllegalArgumentException(NO_CONSUMER_KEY)
		if (key != consumerKey) throw IllegalArgumentException(NO_CONSUMER)

		OAuthConsumer(DEFAULT_CALLBACK_URL, consumerKey, consumerSecret, null).let {
			OAuthAccessor(it).let { oAuthAccessor ->
				try {
					SimpleOAuthValidator().validateMessage(HttpRequestOAuthMessage(request), oAuthAccessor)
				} catch (pe: OAuthProblemException) {
					LOGGER.error(pe.message)
					pe.parameters.keys.forEach { key ->
						LOGGER.error(pe.parameters[key].toString())
					}
					throw pe
				} catch (e: OAuthException) {
					LOGGER.error(e.message)
					throw e
				}
			}
		}
	}
}
