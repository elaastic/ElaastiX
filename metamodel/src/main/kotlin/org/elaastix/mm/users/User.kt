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

package org.elaastix.mm.users

import org.elaastix.mm.MmObject

/**
 * An Elaastix user.
 * MAY be backed by a concrete account, but this is not guaranteed.
 */
interface User : MmObject {
    /**
     * Whether the user has opted into authoring features.
     * Enables the creation of pedagogical material and activities.
     */
    val isWriterModeEnabled: Boolean

    /**
     * Whether the user is a platform administrator.
     * An administrator has **unlimited** rights and MAY bypass all authorisation checks.
     */
    val isAdministrator: Boolean
}
