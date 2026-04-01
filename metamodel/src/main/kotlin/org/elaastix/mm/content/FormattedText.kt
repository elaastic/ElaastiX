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

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

/**
 * A type of content that can only make use of inline formatting (bold, italics, ...).
 * It MUST NOT provide any structural formatting capabilities (e.g. titles, codeblocks...).
 * New lines are not necessarily forbidden, but renderers MUST normalise all whitespace as simple spaces.
 *
 * Implementations MUST losslessly convert from and to a plain [String]. This implies the following invariants:
 * - `FormattedText.fromString(fstr.toString()) == fstr`
 * - For two instances of a same subclass, `fstr1 == fstr2 <=> fstr1.toString() == fstr.toString()`
 *
 * Examples of compliant formats are:
 * - CommonMark (0.31.2) § 6 "Inlines" ONLY, excluding § 6.4 "Images", § 6.6 "Raw HTML", § 6.7 "Hard line breaks".
 * - BBCode (from phpBB), excluding `img`, `list`, `code`, `quote`.
 *
 * **IMPORTANT**: All subclasses MUST have a companion object named `Factory` that inherits [FormattedTextFactory].
 */
interface FormattedText : FormattedContent {
    /**
     * Losslessly convert a formatted text to a String.
     *
     * Type information (the kind of formatted text) is not encoded; it is up to the callee to perform any tagging
     * deemed appropriate.
     */
    override fun toString(): String

    override fun toJson(): JsonElement = JsonPrimitive(toString())

    /** Factory that'll be used by the JPA mapper. */
    interface FormattedTextFactory : FormattedContent.FormattedContentFactory {
        override fun fromJson(json: JsonElement): FormattedText {
            require(json is JsonPrimitive && json.isString) {
                when (json) {
                    is JsonPrimitive -> "Expected a JSON string, got $json"
                    else -> "Expected a JSON string, got ${json::class.simpleName}"
                }
            }

            return fromString(json.content)
        }

        /** Constructs an instance of the content from the stored string. */
        fun fromString(string: String): FormattedText
    }
}
