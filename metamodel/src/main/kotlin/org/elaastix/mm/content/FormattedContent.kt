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

/**
 * A type of content that can be structured and formatted, but MUST NOT make use of advanced and/or custom widgets.
 *
 * This type of content SHOULD always be "mostly-valid"; that is, anything thrown at it should render and be readable
 * on a best-effort basis. In other words, implementations SHOULD take resiliency seriously and implement recovery
 * mechanisms if needed.
 *
 * Formats that would be appropriate for this type of content are for example:
 * CommonMark, Obsidian Flavoured Markdown¹ (excluding tasks and embedded HTML), AsciiDoc, ...
 *
 * **IMPORTANT**: All subclasses MUST have a companion object named `Factory` that inherits [FormattedContentFactory].
 *
 * 1: OFM states support for LaTeX, but it actually only supports math-related macros. This is the desired behaviour.
 *    See the MathJax documentation: https://docs.mathjax.org/en/v4.1/input/tex/differences.html
 */
interface FormattedContent : RichContent {
    /** Factory that'll be used by the JPA mapper. */
    interface Factory : RichContent.Factory {
        override fun fromJson(json: JsonElement): FormattedContent
    }
}
