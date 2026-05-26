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

package org.elaastix.commons.security

/**
 * Permissions that can be granted to users.
 * An authority is roughly equivalent to a *permission*, a finer-grained primitive than roles.
 *
 * @see [Role]
 */
enum class Permission : Authority {
	/** Create and manage Remote Activities registrations. */
	REMOTE_ACTIVITY,

	/** Create materials (outside the context of an activity as a participant). */
	CREATE_BASE_MATERIAL,

	/** Publish materials to the public. */
	PUBLISH_BASE_MATERIAL,

	/** Create scenarios. */
	CREATE_SCENARIOS,

	/** Create sequences. */
	CREATE_SEQUENCES,

	/** Create assignments. */
	CREATE_ASSIGNMENTS,

	/** Participate in assignments. */
	PARTICIPATE_IN_ASSIGNMENTS,
}
