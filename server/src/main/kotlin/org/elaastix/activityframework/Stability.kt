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

package org.elaastix.activityframework

/**
 * Enum for describing the stability state of an activity.
 */
enum class Stability {
	/**
	 * The activity is unstable, breaking changes may occur, or the activity may be removed without notice.
	 * Breaking changes may cause forceful removal of scenarios and/or associated data.
	 */
	EXPERIMENTAL,

	/**
	 * The activity is stable and actively maintained.
	 */
	STABLE,

	/**
	 * Active development has ceased. No new features will be added, and only severe bugs might be addressed.
	 * Users are encouraged to migrate away. Creation of new scenarios using these activities is disabled.
	 */
	DEPRECATED,

	/**
	 * No longer available. Placeholder to preserve the existing scenarios' integrity, but will no longer work.
	 * Users are strongly encouraged to migrate resources depending on retired features, or they face
	 * the risk of data loss once the feature is permanently removed.
	 */
	RETIRED,
}
