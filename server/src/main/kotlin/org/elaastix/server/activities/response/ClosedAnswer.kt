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

package org.elaastix.server.activities.response

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Wrapper class encapsulating a closed answer.
 * Designed to improve readability between single-choice and multiple-choice answers and to enforce invariants.
 */
@JvmInline
@Serializable(with = ClosedAnswer.ClosedAnswerSerializer::class)
value class ClosedAnswer private constructor(
	/** Indices in the question's response array. */
	private val value: List<UInt>,
) {
	/** Factory methods associated with the closed answer type. */
	companion object {
		/** Creates a closed answer for a single-choice closed question. */
		fun single(answer: UInt?) = ClosedAnswer(answer?.let { listOf(it) } ?: emptyList())

		/** Creates a closed answer for a multiple-choice closed question. */
		fun multiple(answer: List<UInt>) = ClosedAnswer(answer)
	}

	/** Returns whether the answer has multiple choices set or not. */
	fun hasMultiple() = value.size > 1

	/**
	 * Returns the answer unwrapped as a single choice.
	 * @throws IllegalStateException if there are multiple choices selected.
	 */
	fun asSingle() = check(value.size > 1) { "Cannot unwrap as a single answer!" }.let { value.firstOrNull() }

	/** Returns the answer unwrapped as a multiple choice. */
	fun asMultiple() = value

	/** Kotlinx serializer for [ClosedAnswer]. */
	class ClosedAnswerSerializer : KSerializer<ClosedAnswer> {
		private val delegate = ListSerializer(UInt.serializer())

		override val descriptor = SerialDescriptor("org.elaastix.activities.response.ClosedAnswer", delegate.descriptor)

		override fun serialize(encoder: Encoder, value: ClosedAnswer) = encoder.encodeSerializableValue(delegate, value.value)

		override fun deserialize(decoder: Decoder) = ClosedAnswer(decoder.decodeSerializableValue(delegate))
	}
}
