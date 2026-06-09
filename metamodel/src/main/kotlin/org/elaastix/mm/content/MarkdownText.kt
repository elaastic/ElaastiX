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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Plain text formatted in Markdown (inline syntax elements only).
 *
 * CommonMark (0.31.2) § 6 "Inlines" ONLY, excluding § 6.4 "Images", § 6.6 "Raw HTML", § 6.7 "Hard line breaks".
 *
 * @property content The Markdown-inline-formatted text.
 */
@Serializable
@SerialName("MarkdownInline")
data class MarkdownText(val content: String) : FormattedText
