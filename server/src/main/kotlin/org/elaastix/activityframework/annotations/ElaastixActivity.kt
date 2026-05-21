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

package org.elaastix.activityframework.annotations

import exalib.session.ActivitySession
import org.elaastix.activityframework.Stability
import kotlin.reflect.KClass

/**
 * Indicates that the class is an ElaastiX Activity.
 * EXAs registered within the Spring context will be picked up by the engine and automatically configured and routed.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ElaastixActivity(
	/**
	 * The *Domain Authority* for the activity. Will be used to build endpoints NSIDs.
	 * MUST conform to the [Namespaced Identifiers](https://atproto.com/specs/nsid) rules.
	 */
	val authority: String = "",

	/**
	 * The [ActivitySession] class for the activity.
	 * Session allocation and outer lifecycle are managed by the engine.
	 *
	 * @see ActivitySession
	 */
	val session: KClass<out ActivitySession> = ActivitySession::class,

	/**
	 * Stability contract for the activity.
	 */
	val stability: Stability = Stability.STABLE,
)
