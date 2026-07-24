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

package org.elaastix.commons.ws

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.serializer
import org.springframework.web.socket.BinaryMessage
import org.springframework.web.socket.WebSocketMessage

/**
 * Helper responsible for turning a serializable payload into a [WebSocketMessage].
 */
@OptIn(ExperimentalSerializationApi::class)
class WebSocketMessageFormatter(private val cbor: Cbor) {
	/**
	 * Transforms a raw payload into a [WebSocketMessage] that can be sent off.
	 */
	inline fun <reified T : Any> format(payload: T) = formatWithSerializer(payload, serializer())

	@PublishedApi
	internal fun <T> formatWithSerializer(payload: T, serializer: KSerializer<T>): BinaryMessage {
		val bytes = cbor.encodeToByteArray(serializer, payload)
		val serialName = serializer.descriptor.serialName

		// Encoding:
		// Array(2) [ String(n) "payload name", ... payload ]
		val preamble = when (serialName.length) {
			in 0..23 -> byteArrayOf((0x80 or 0x02).toByte(), (0x60 or serialName.length).toByte())
			in 24..255 -> byteArrayOf((0x80 or 0x02).toByte(), 0x78.toByte(), serialName.length.toByte())
			else -> error("Serial name too long: $serialName")
		}

		val serialNameBytes = serialName.toByteArray()
		return BinaryMessage(preamble + serialNameBytes + bytes)
	}
}
