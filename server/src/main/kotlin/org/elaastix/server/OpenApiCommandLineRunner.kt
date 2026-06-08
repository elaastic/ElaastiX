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

package org.elaastix.server

import io.swagger.v3.oas.models.OpenAPI
import org.slf4j.LoggerFactory
import org.springdoc.webmvc.api.OpenApiResource
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Fallback
import org.springframework.context.annotation.Profile
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.security.crypto.encrypt.BytesEncryptor
import org.springframework.stereotype.Component
import java.io.File
import java.util.Locale
import kotlin.system.exitProcess

/**
 * Runner to generate the OpenAPI specification file and write it to disk.
 * Immediately exits once ran.
 *
 * Best used via `just openapi`.
 */
@Component
@Profile("openapi")
@Order(Ordered.HIGHEST_PRECEDENCE)
class OpenApiCommandLineRunner(
	@Value($$"${generation-path}")
	private val generationPath: String,
	private val openApiResource: OpenApiResource,
) : CommandLineRunner {
	override fun run(vararg args: String) {
		val logger = LoggerFactory.getLogger(this::class.java)
		logger.info("Generating OpenAPI specification. The server will exit once this is complete.")

		val json = generate()
		val file = File(generationPath)
		val os = file.outputStream()
		os.write(json)
		os.close()

		logger.info("Specification written to ${file.absolutePath}. Bye.")
		exitProcess(0)
	}

	private fun generate(): ByteArray {
		// It's protected. And putting together a fake HttpServletRequest is worse. Deal with it. >:(
		val clazz = OpenApiResource::class.java.superclass
		val getSpec = clazz.getDeclaredMethod("getOpenApi", String::class.java, Locale::class.java)
		getSpec.trySetAccessible()

		val writeJsonValue = clazz.getDeclaredMethod("writeJsonValue", OpenAPI::class.java)
		writeJsonValue.trySetAccessible()

		val spec = getSpec.invoke(openApiResource, "", Locale.UK) as OpenAPI
		return writeJsonValue.invoke(openApiResource, spec) as ByteArray
	}

	@Bean
	@Fallback
	@ConditionalOnMissingBean(BytesEncryptor::class)
	fun noopEncryptor(): BytesEncryptor = object : BytesEncryptor {
		override fun encrypt(byteArray: ByteArray) = ByteArray(0)
		override fun decrypt(encryptedByteArray: ByteArray) = ByteArray(0)
	}
}
