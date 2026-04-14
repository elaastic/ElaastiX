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

import io.mockk.bdd.given
import io.mockk.bdd.then
import io.mockk.mockkObject
import io.mockk.spyk
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.assertj.core.api.Assertions.assertThat
import org.elaastix.mm.content.internal.ContentJpaConverter
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ContentJpaConverterTest {
    val converter = ContentJpaConverter()

    companion object {
        class TestRichContent(val data: Map<String, JsonElement>) : RichContent {
            override fun toJson(): JsonElement = JsonObject(data)

            companion object Factory : RichContent.Factory {
                override fun fromJson(json: JsonElement): RichContent {
                    require(json is JsonObject) {
                        "Invalid JsonElement (expected JsonObject got ${json::class.simpleName})"
                    }

                    return TestRichContent(json)
                }
            }
        }

        class TestFormattedContent(val data: String) : FormattedContent {
            override fun toJson(): JsonElement = JsonPrimitive(data)

            companion object Factory : FormattedContent.Factory {
                override fun fromJson(json: JsonElement): FormattedContent {
                    require(json is JsonPrimitive) {
                        "Invalid JsonElement (expected JsonPrimitive got ${json::class.simpleName})"
                    }

                    require(json.isString) {
                        "Invalid JsonText (expected a String got: $json)"
                    }

                    return TestFormattedContent(json.content)
                }
            }
        }

        class TestFormattedText(val text: String) : FormattedText {
            override fun toString(): String = text

            companion object Factory : FormattedText.Factory {
                override fun fromString(string: String): FormattedText = TestFormattedText(string)
            }
        }

        @Suppress("unused") // Used via reflection
        class BadRichContentNoFactory(val data: Map<String, JsonElement>) : RichContent {
            override fun toJson(): JsonElement = JsonObject(data)
        }

        @Suppress("unused") // Used via reflection
        class BadRichContentBadFactory(val data: Map<String, JsonElement>) : RichContent {
            override fun toJson(): JsonElement = JsonObject(data)

            companion object Factory
        }
    }

    @Test
    fun `serialises rich content as a plain JSON object`() {
        val content = spyk(
            TestRichContent(
                mapOf("some" to JsonPrimitive("data")),
            ),
        )

        val result = converter.convertToDatabaseColumn(content)

        then(exactly = 1) { content.toJson() }
        assertThat(result).isEqualTo(
            $$"""{"c":"org.elaastix.mm.content.ContentJpaConverterTest$Companion$TestRichContent",""" +
                """"d":{"some":"data"}}""",
        )
    }

    @Test
    fun `serialises formatted content as a plain JSON object`() {
        val content = spyk(
            TestFormattedContent("some text"),
        )

        val result = converter.convertToDatabaseColumn(content)

        then(exactly = 1) { content.toJson() }
        assertThat(result).isEqualTo(
            $$"""{"c":"org.elaastix.mm.content.ContentJpaConverterTest$Companion$TestFormattedContent",""" +
                """"d":"some text"}""",
        )
    }

    @Test
    fun `serialises formatted text as a plain JSON object`() {
        val content = spyk(
            TestFormattedText(
                "some plaintext",
            ),
        )

        // MockK's spy interferes with toString... :/
        given { content.toString() } answers { content.text }

        val result = converter.convertToDatabaseColumn(content)

        then(exactly = 1) { content.toJson() }
        then(exactly = 1) { content.toString() }
        assertThat(result).isEqualTo(
            $$"""{"c":"org.elaastix.mm.content.ContentJpaConverterTest$Companion$TestFormattedText",""" +
                """"d":"some plaintext"}""",
        )
    }

    @Test
    fun `deserialises rich content from a plain JSON object`() {
        mockkObject(TestRichContent.Factory)
        given { TestRichContent.fromJson(any()) } answers { callOriginal() }

        val result = converter.convertToEntityAttribute(
            $$"""{"c":"org.elaastix.mm.content.ContentJpaConverterTest$Companion$TestRichContent",""" +
                """"d":{"some":"data"}}""",
        )

        then(exactly = 1) { TestRichContent.fromJson(any()) }

        assertThat(result).isInstanceOf(TestRichContent::class.java)
        val content = result as TestRichContent

        assertThat(content.data).isEqualTo(mapOf("some" to JsonPrimitive("data")))
    }

    @Test
    fun `deserialises formatted content from a plain JSON object`() {
        mockkObject(TestFormattedContent.Factory)
        given { TestFormattedContent.fromJson(any()) } answers { callOriginal() }

        val result = converter.convertToEntityAttribute(
            $$"""{"c":"org.elaastix.mm.content.ContentJpaConverterTest$Companion$TestFormattedContent",""" +
                """"d":"some text"}""",
        )

        then(exactly = 1) { TestFormattedContent.fromJson(any()) }

        assertThat(result).isInstanceOf(TestFormattedContent::class.java)
        val content = result as TestFormattedContent

        assertThat(content.data).isEqualTo("some text")
    }

    @Test
    fun `deserialises formatted text from a plain JSON object`() {
        mockkObject(TestFormattedText.Factory)
        given { TestFormattedText.fromJson(any()) } answers { callOriginal() }

        val result = converter.convertToEntityAttribute(
            $$"""{"c":"org.elaastix.mm.content.ContentJpaConverterTest$Companion$TestFormattedText",""" +
                """"d":"some plaintext"}""",
        )

        then(exactly = 1) { TestFormattedText.fromJson(any()) }

        assertThat(result).isInstanceOf(TestFormattedText::class.java)
        val content = result as TestFormattedText

        assertThat(content.text).isEqualTo("some plaintext")
    }

    @Test
    fun `does not deserialises objects without a Factory`() {
        assertThrows<IllegalStateException> {
            converter.convertToEntityAttribute(
                $$"""{"c":"org.elaastix.mm.content.ContentJpaConverterTest$Companion$BadRichContentNoFactory",""" +
                    """"d":"some plaintext"}""",
            )
        }
    }

    @Test
    fun `does not deserialises objects with an improper Factory`() {
        assertThrows<IllegalStateException> {
            converter.convertToEntityAttribute(
                $$"""{"c":"org.elaastix.mm.content.ContentJpaConverterTest$Companion$BadRichContentBadFactory",""" +
                    """"d":"some plaintext"}""",
            )
        }
    }
}
