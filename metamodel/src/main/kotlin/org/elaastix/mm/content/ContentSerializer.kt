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

package org.elaastix.mm.content

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import org.elaastix.mm.content.ContentTypesRegistry.registerContentType
import org.elaastix.mm.content.ContentTypesRegistry.registerContentTypeAlias
import kotlin.reflect.KClass

/** Registry for all types of [RichContent], [FormattedContent], and [FormattedText]. */
object ContentTypesRegistry {
	// COVERAGE: inline reified functions suffer from reporting issues. They can't be excluded either.
	// See: https://github.com/Kotlin/kotlinx-kover/issues/753

	internal typealias ContentFactory<T> = (JsonElement) -> T
	internal typealias PlaintextFactory<T> = (String) -> T

	internal val idToClazz = mutableMapOf<String, KClass<*>>()
	internal val clazzToId = mutableMapOf<KClass<*>, String>()
	internal val clazzToFactory = mutableMapOf<KClass<*>, ContentFactory<RichContent>>()

	/**
	 * Registers a type of content.
	 *
	 * Beware, [id] **is an ABI contract** and changing it **will break compatibility with data from prior versions**.
	 * This means that changing it carelessly can render large pans of data in the database **unreadable**.
	 *
	 * When changing the ID, make sure to register it as a secondary alias via [registerContentTypeAlias], so existing
	 * records and old clients can still communicate with the server.
	 *
	 * @param T The class to register.
	 * @param id The ID to use as discriminator for this class type.
	 * @param factory The factory used to get an instance of [T] from a (**potentially unsafe**) [JsonElement].
	 * @throws IllegalArgumentException if the ID is already in use.
	 * @throws IllegalArgumentException if the class is already registered.
	 */
	inline fun <reified T : RichContent> registerContentType(id: String, noinline factory: ContentFactory<T>) =
		registerContentType(id, T::class, factory)

	/**
	 * Registers a type of content that is plaintext specifically.
	 *
	 * Similar to [registerContentType], with a different type of factory.
	 *
	 * @param T The class to register.
	 * @param id The ID to use as discriminator for this class type. **API/ABI contract**.
	 * @param factory The factory used to get an instance of [T] from a (**potentially unsafe**) [JsonElement].
	 * @throws IllegalArgumentException if the ID is already in use.
	 * @throws IllegalArgumentException if the class is already registered.
	 */
	inline fun <reified T : RichContent> registerPlaintextType(id: String, crossinline factory: PlaintextFactory<T>) =
		registerPlaintextType(id, T::class, factory)

	/**
	 * Registers an alias for a given type of content. Useful for legacy aliases that still need to be resolved.
	 *
	 * @param T The class to register.
	 * @param id The ID to use as discriminator for this class type. **API/ABI contract**.
	 * @throws IllegalArgumentException if the ID is already in use.
	 * @throws IllegalArgumentException if the class does not have a primary registration.
	 * @see registerContentType
	 */
	inline fun <reified T : RichContent> registerContentTypeAlias(id: String) = registerContentTypeAlias(id, T::class)

	/** Shortcut for registering content types with the ID predefined as the class's simple name. */
	inline fun <reified T : RichContent> registerContentType(noinline factory: ContentFactory<T>) =
		registerContentType(T::class.java.simpleName, T::class, factory)

	/** Shortcut for registering content types with the ID predefined as the class's simple name. */
	inline fun <reified T : RichContent> registerPlaintextType(noinline factory: PlaintextFactory<T>) =
		registerPlaintextType(T::class.java.simpleName, T::class, factory)

	/** Non-reified version of registerContentType. */
	fun <T : RichContent> registerContentType(id: String, clazz: KClass<T>, factory: ContentFactory<T>) {
		require(!idToClazz.containsKey(id)) {
			"Tried to register class $clazz with id '$id', which conflicts with ${idToClazz[id]!!}"
		}

		require(!clazzToId.containsKey(clazz)) {
			"Class $clazz is already registered. Use registerContentTypeAlias instead."
		}

		idToClazz[id] = clazz
		clazzToId[clazz] = id
		clazzToFactory[clazz] = factory
	}

	/** Non-reified version of registerContentTypeAlias. */
	fun <T : RichContent> registerContentTypeAlias(id: String, clazz: KClass<T>) {
		require(!idToClazz.containsKey(id)) {
			"Tried to register class $clazz with secondary alias '$id', which conflicts with ${idToClazz[id]!!}"
		}

		require(clazzToId.containsKey(clazz)) {
			"No primary registration exists for $clazz. Use registerContentType first."
		}

		idToClazz[id] = clazz
	}

	/** Non-reified version of registerPlaintextType. */
	@Suppress("detekt:FunctionSignature") // For whatever reason ktlint is having a very hard time here??
	inline fun <T : RichContent> registerPlaintextType(
		id: String,
		clazz: KClass<T>,
		crossinline factory: PlaintextFactory<T>,
	) = registerContentType(id, clazz) {
		require(it is JsonPrimitive && it.isString) {
			"Expected a JSON string, got " +
				when (it) {
					is JsonPrimitive -> it.content
					else -> it::class.simpleName
				}
		}

		factory.invoke(it.content)
	}
}

/**
 * Kotlinx serializer for all types of content. Currently only compatible with JSON.
 *
 * TODO: Add support for CBOR serialization. Currently not supported due to the use of [JsonElement].
 */
abstract class AbstractContentSerializer<T : RichContent> internal constructor() : KSerializer<T> {
	internal val delegate = ContentWrapper.serializer()

	override fun serialize(encoder: Encoder, value: T) =
		encoder.encodeSerializableValue(
			delegate,
			ContentWrapper(
				clazz = value.getContentClassId(),
				data = value.toJson(),
			),
		)

	override fun deserialize(decoder: Decoder): T {
		val wrapper: ContentWrapper = decoder.decodeSerializableValue(delegate)

		@Suppress("UNCHECKED_CAST") // We don't really have much of a choice.
		return wrapper.clazz.buildContent(wrapper.data) as T
	}

	private fun <T : RichContent> T.getContentClassId(): String =
		ContentTypesRegistry.clazzToId[this::class] ?: error("Unregistered content type ${this::class}")

	private fun String.getContentClass(): KClass<*> =
		ContentTypesRegistry.idToClazz[this] ?: error("Unregistered content type id $this")

	// COVERAGE: error branch is unreachable
	private fun <T : KClass<*>> T.getFactory(): ContentTypesRegistry.ContentFactory<RichContent> =
		ContentTypesRegistry.clazzToFactory[this] ?: error("Unregistered content type $this")

	private fun <T : KClass<*>> T.buildContent(json: JsonElement): RichContent = getFactory().invoke(json)

	private fun String.buildContent(json: JsonElement): RichContent = getContentClass().buildContent(json)

	@Serializable
	internal data class ContentWrapper(
		@SerialName("c")
		val clazz: String,
		@SerialName("d")
		val data: JsonElement,
	)
}

/** Serializer for [RichContent]. */
class RichContentSerializer : AbstractContentSerializer<RichContent>() {
	override val descriptor = SerialDescriptor("org.elaastix.mm.RichContent", delegate.descriptor)
}

/** Serializer for [FormattedContent]. */
class FormattedContentSerializer : AbstractContentSerializer<FormattedContent>() {
	override val descriptor = SerialDescriptor("org.elaastix.mm.FormattedContent", delegate.descriptor)
}

/** Serializer for [FormattedText]. */
class FormattedTextSerializer : AbstractContentSerializer<FormattedText>() {
	override val descriptor = SerialDescriptor("org.elaastix.mm.FormattedText", delegate.descriptor)
}
