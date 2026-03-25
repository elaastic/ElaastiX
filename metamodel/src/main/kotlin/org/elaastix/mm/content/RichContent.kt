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

/**
 * A rich content is the least restricted type of content in ElaastiX, with no limits on what it allows.
 * Implementations are allowed to provide highly advanced interactive widgets if they desire.
 *
 * Example of rich content formats that would belong to this category:
 * customised TipTap documents, Typst, LaTeX, ...
 *
 * Appropriate when authoring complex resources that can make use of the features it provides. In cases where
 * only formatting capabilities are desired, [FormattedContent] is more appropriate and a lot more robust against
 * accidental (or malicious) misuse.
 *
 * @see FormattedContent
 */
interface RichContent
