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

import io.mockk.bdd.then
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.spyk
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.api.parallel.Isolated

// We could use fine-grained concurrency locking with ResourceLocks, but that's not as foolproof.
@Isolated("Registry is a globally-visible singleton")
class ContentSerializersTest {
	typealias Factory<T> = ContentTypesRegistry.ContentFactory<T>
	typealias FactoryText<T> = ContentTypesRegistry.PlaintextFactory<T>

	companion object {
		val testRichContentFactory: Factory<TestRichContent>
		val testFormattedContentFactory: FactoryText<TestFormattedContent>
		val testFormattedContentCustomIdFactory: FactoryText<TestFormattedContentCustomId>
		val testFormattedTextFactory: FactoryText<TestFormattedText>

		open class TestRichContent(val data: Map<String, JsonElement>) : RichContent {
			override fun toJson(): JsonElement = JsonObject(data)
		}

		class TestFormattedContent(val data: String) : FormattedContent {
			override fun toJson(): JsonElement = JsonPrimitive(data)
		}

		class TestFormattedContentCustomId(val data: String) : FormattedContent {
			override fun toJson(): JsonElement = JsonPrimitive(data)
		}

		class TestFormattedText(val text: String) : FormattedText {
			override fun asString(): String = text
		}

		class BadNotRegistered(val data: Map<String, JsonElement>) : RichContent {
			override fun toJson(): JsonElement = JsonObject(data)
		}

		// These types need to already exist and be stable across tests.
		typealias FakeFactory1 = ContentTypesRegistry.ContentFactory<FakeContentType1>
		typealias FakeFactory2 = ContentTypesRegistry.ContentFactory<FakeContentType2>

		class FakeContentType1 : TestRichContent(emptyMap())
		class FakeContentType2 : TestRichContent(emptyMap())

		const val FAKE_CONTENT_ID_1 = "A"
		const val FAKE_CONTENT_ID_2 = "B"
		const val FAKE_CONTENT_ID_3 = "C"
		const val CONTENT_ALIAS_ID = "Alias"

		@JvmStatic
		@AfterAll
		fun `clear registrations`() {
			ContentTypesRegistry.idToClazz.remove(FAKE_CONTENT_ID_1)
			ContentTypesRegistry.idToClazz.remove(FAKE_CONTENT_ID_2)
			ContentTypesRegistry.idToClazz.remove(FAKE_CONTENT_ID_3)
			ContentTypesRegistry.idToClazz.remove(CONTENT_ALIAS_ID)
			ContentTypesRegistry.idToClazz.remove(FakeContentType1::class.java.simpleName)
			ContentTypesRegistry.idToClazz.remove(FakeContentType2::class.java.simpleName)

			ContentTypesRegistry.clazzToId.remove(FakeContentType1::class)
			ContentTypesRegistry.clazzToId.remove(FakeContentType2::class)

			ContentTypesRegistry.clazzToFactory.remove(FakeContentType1::class)
			ContentTypesRegistry.clazzToFactory.remove(FakeContentType2::class)
		}

		@JvmStatic
		@AfterAll
		fun `clear static registrations`() {
			ContentTypesRegistry.idToClazz.remove(TestRichContent::class.java.simpleName)
			ContentTypesRegistry.idToClazz.remove(TestFormattedContent::class.java.simpleName)
			ContentTypesRegistry.idToClazz.remove(TestFormattedContentCustomId::class.java.simpleName)
			ContentTypesRegistry.idToClazz.remove(TestFormattedText::class.java.simpleName)

			ContentTypesRegistry.clazzToId.remove(TestRichContent::class)
			ContentTypesRegistry.clazzToId.remove(TestFormattedContent::class)
			ContentTypesRegistry.clazzToId.remove(TestFormattedContentCustomId::class)
			ContentTypesRegistry.clazzToId.remove(TestFormattedText::class)

			ContentTypesRegistry.clazzToFactory.remove(TestRichContent::class)
			ContentTypesRegistry.clazzToFactory.remove(TestFormattedContent::class)
			ContentTypesRegistry.clazzToFactory.remove(TestFormattedContentCustomId::class)
			ContentTypesRegistry.clazzToFactory.remove(TestFormattedText::class)
		}

		private inline fun <reified T : RichContent> mockFactory(crossinline lambda: (JsonElement) -> T) =
			mockk<(JsonElement) -> T> {
				val arg = slot<JsonElement>()
				every { this@mockk.invoke(capture(arg)) } answers { lambda(arg.captured) }
			}

		private inline fun <reified T : RichContent> mockTextFactory(crossinline lambda: (String) -> T) =
			mockk<(String) -> T> {
				val arg = slot<String>()
				every { this@mockk.invoke(capture(arg)) } answers { lambda(arg.captured) }
			}

		init {
			ContentTypesRegistry.registerContentType(
				mockFactory {
					require(it is JsonObject) {
						"Invalid JsonElement (expected JsonObject got ${it::class.simpleName})"
					}

					TestRichContent(it)
				}.also { testRichContentFactory = it },
			)

			ContentTypesRegistry.registerPlaintextType(
				mockTextFactory {
					TestFormattedContent(it)
				}.also { testFormattedContentFactory = it },
			)

			ContentTypesRegistry.registerPlaintextType(
				"AwesomeContent",
				mockTextFactory {
					TestFormattedContentCustomId(it)
				}.also { testFormattedContentCustomIdFactory = it },
			)

			ContentTypesRegistry.registerPlaintextType(
				mockTextFactory { TestFormattedText(it) }
					.also { testFormattedTextFactory = it },
			)
		}
	}

	@BeforeEach
	fun `reset spies`() {
		clearMocks(
			testRichContentFactory,
			testFormattedContentFactory,
			testFormattedContentCustomIdFactory,
			testFormattedTextFactory,
			answers = false,
		)
	}

	@Test
	fun `serialises rich content as a plain JSON object`() {
		val content: RichContent = spyk(
			TestRichContent(
				mapOf("some" to JsonPrimitive("data")),
			),
		)

		val result = Json.encodeToString(content)

		then(exactly = 1) { content.toJson() }
		assertThat(result).isEqualTo(
			"""{"c":"TestRichContent","d":{"some":"data"}}""",
		)
	}

	@Test
	fun `serialises formatted content as a plain JSON object`() {
		val content: FormattedContent = spyk(
			TestFormattedContent("some text"),
		)

		val result = Json.encodeToString(content)

		then(exactly = 1) { content.toJson() }
		assertThat(result).isEqualTo(
			"""{"c":"TestFormattedContent","d":"some text"}""",
		)
	}

	@Test
	fun `serialises formatted text as a plain JSON object`() {
		val content: FormattedText = spyk(
			TestFormattedText(
				"some plaintext",
			),
		)

		val result = Json.encodeToString(content)

		then(exactly = 1) { content.toJson() }
		then(exactly = 1) { content.asString() }
		assertThat(result).isEqualTo(
			"""{"c":"TestFormattedText","d":"some plaintext"}""",
		)
	}

	@Test
	fun `serialises content with the correct custom identifier`() {
		val content: FormattedContent = spyk(
			TestFormattedContentCustomId("some text"),
		)

		val result = Json.encodeToString(content)

		then(exactly = 1) { content.toJson() }
		assertThat(result).isEqualTo(
			"""{"c":"AwesomeContent","d":"some text"}""",
		)
	}

	@Test
	fun `does not serialises unregistered content type`() {
		val content: RichContent = spyk(
			BadNotRegistered(emptyMap()),
		)

		assertThrows<IllegalStateException> {
			Json.encodeToString(content)
		}

		then(exactly = 0) { content.toJson() }
	}

	@Test
	fun `deserialises rich content from a plain JSON object`() {
		val result: RichContent = Json.decodeFromString(
			"""{"c":"TestRichContent","d":{"some":"data"}}""",
		)

		then(exactly = 1) { testRichContentFactory(any()) }

		assertThat(result).isInstanceOf(TestRichContent::class.java)
		val content = result as TestRichContent

		assertThat(content.data).isEqualTo(mapOf("some" to JsonPrimitive("data")))
	}

	@Test
	fun `deserialises formatted content from a plain JSON object`() {
		val result: FormattedContent = Json.decodeFromString(
			"""{"c":"TestFormattedContent","d":"some text"}""",
		)

		then(exactly = 1) { testFormattedContentFactory(any()) }

		assertThat(result).isInstanceOf(TestFormattedContent::class.java)
		val content = result as TestFormattedContent

		assertThat(content.data).isEqualTo("some text")
	}

	@Test
	fun `deserialises formatted text from a plain JSON object`() {
		val result: FormattedText = Json.decodeFromString(
			"""{"c":"TestFormattedText","d":"some plaintext"}""",
		)

		then(exactly = 1) { testFormattedTextFactory.invoke(any()) }

		assertThat(result).isInstanceOf(TestFormattedText::class.java)
		val content = result as TestFormattedText

		assertThat(content.text).isEqualTo("some plaintext")
	}

	@Test
	fun `does not deserialises objects with an unregistered content type`() {
		assertThrows<IllegalStateException> {
			Json.decodeFromString<RichContent>(
				"""{"c":"BadNotRegistered","d":"some plaintext"}""",
			)
		}
	}

	@Test
	fun `does not deserialises formatted text from a complex JSON object`() {
		assertThrows<IllegalArgumentException> {
			Json.decodeFromString<FormattedText>(
				"""{"c":"TestFormattedText","d":{"wow":"meow"}}""",
			)
		}
	}

	@Test
	fun `does not deserialises formatted text from a non-string primitive JSON element`() {
		assertThrows<IllegalArgumentException> {
			Json.decodeFromString<FormattedText>(
				"""{"c":"TestFormattedText","d":false}""",
			)
		}
	}

	@Nested
	@Execution(ExecutionMode.SAME_THREAD, reason = "Write access to the global registry")
	inner class WithExtraContentTypes {
		@BeforeEach
		fun `clear registrations`() = Companion.`clear registrations`()

		@Test
		fun `serialises content with its primary type name when an alias exists`() {
			ContentTypesRegistry.registerContentTypeAlias(FAKE_CONTENT_ID_1, TestFormattedText::class)

			val content: FormattedContent = TestFormattedContent("some text")
			val json = Json.encodeToString(content)
			assertThat(json).isEqualTo(
				"""{"c":"TestFormattedContent","d":"some text"}""",
			)
		}

		@Test
		fun `deserialises content with an alias specified in the payload`() {
			ContentTypesRegistry.registerContentTypeAlias(CONTENT_ALIAS_ID, TestFormattedText::class)

			val result: FormattedText = Json.decodeFromString(
				"""{"c":"Alias","d":"some plaintext"}""",
			)

			assertThat(result).isInstanceOf(TestFormattedText::class.java)
			assertThat((result as TestFormattedText).text).isEqualTo("some plaintext")
		}

		@Test
		fun `does not accept registering conflicting IDs (primary + primary)`() {
			assertDoesNotThrow {
				ContentTypesRegistry.registerContentType(
					FAKE_CONTENT_ID_1,
					FakeContentType1::class,
					mockk<FakeFactory1>(),
				)
			}
			assertThrows<IllegalArgumentException> {
				ContentTypesRegistry.registerContentType(
					FAKE_CONTENT_ID_1,
					FakeContentType2::class,
					mockk<FakeFactory2>(),
				)
			}
		}

		@Test
		fun `does not accept registering conflicting IDs (primary + secondary)`() {
			assertDoesNotThrow {
				ContentTypesRegistry.registerContentType(
					FAKE_CONTENT_ID_1,
					FakeContentType1::class,
					mockk<FakeFactory1>(),
				)
			}
			assertDoesNotThrow {
				ContentTypesRegistry.registerContentType(
					FAKE_CONTENT_ID_2,
					FakeContentType2::class,
					mockk<FakeFactory2>(),
				)
			}
			assertThrows<IllegalArgumentException> {
				ContentTypesRegistry.registerContentTypeAlias(FAKE_CONTENT_ID_1, FakeContentType2::class)
			}
		}

		@Test
		fun `does not accept registering conflicting IDs (secondary + primary)`() {
			assertDoesNotThrow {
				ContentTypesRegistry.registerContentType(
					FAKE_CONTENT_ID_1,
					FakeContentType1::class,
					mockk<FakeFactory1>(),
				)
			}
			assertDoesNotThrow {
				ContentTypesRegistry.registerContentTypeAlias(FAKE_CONTENT_ID_2, FakeContentType1::class)
			}
			assertThrows<IllegalArgumentException> {
				ContentTypesRegistry.registerContentType(
					FAKE_CONTENT_ID_2,
					FakeContentType2::class,
					mockk<FakeFactory2>(),
				)
			}
		}

		@Test
		fun `does not accept registering conflicting IDs (secondary + secondary)`() {
			assertDoesNotThrow {
				ContentTypesRegistry.registerContentType(
					FAKE_CONTENT_ID_1,
					FakeContentType1::class,
					mockk<FakeFactory1>(),
				)
			}
			assertDoesNotThrow {
				ContentTypesRegistry.registerContentType(
					FAKE_CONTENT_ID_2,
					FakeContentType2::class,
					mockk<FakeFactory2>(),
				)
			}
			assertDoesNotThrow {
				ContentTypesRegistry.registerContentTypeAlias(FAKE_CONTENT_ID_3, FakeContentType1::class)
			}
			assertThrows<IllegalArgumentException> {
				ContentTypesRegistry.registerContentType(
					FAKE_CONTENT_ID_3,
					FakeContentType2::class,
					mockk<FakeFactory2>(),
				)
			}
		}

		@Test
		fun `does not accept registering the same class twice`() {
			assertDoesNotThrow {
				ContentTypesRegistry.registerContentType(
					FAKE_CONTENT_ID_1,
					FakeContentType1::class,
					mockk<FakeFactory1>(),
				)
			}
			assertThrows<IllegalArgumentException> {
				ContentTypesRegistry.registerContentType(
					FAKE_CONTENT_ID_2,
					FakeContentType1::class,
					mockk<FakeFactory1>(),
				)
			}
		}

		@Test
		fun `does not accept aliasing an unregistered class`() {
			assertThrows<IllegalArgumentException> {
				ContentTypesRegistry.registerContentTypeAlias(FAKE_CONTENT_ID_2, FakeContentType1::class)
			}
		}
	}
}
