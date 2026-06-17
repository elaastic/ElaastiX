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

@file:Suppress("VariableNaming", "PropertyName")

package org.elaastix.server.scenario.exec

import org.elaastix.commons.platform.debt.SciconumTechDebt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@SciconumTechDebt
interface ScenarioConstants {
	val ANSWER_PHASE_DURATION: Duration
	val PEER_PHASE_DURATION: Duration
	val REVISE_PHASE_DURATION: Duration
	val FEEDBACK_PHASE_DURATION: Duration
}

@SciconumTechDebt
object Control : ScenarioConstants {
	override val ANSWER_PHASE_DURATION = 2.minutes
	override val PEER_PHASE_DURATION get() = error("Peer phase is not a thing in control!!")
	override val REVISE_PHASE_DURATION get() = error("Revise phase is not a thing in control!!")
	override val FEEDBACK_PHASE_DURATION = 0.5.minutes
}

@SciconumTechDebt
object Assessment : ScenarioConstants {
	override val ANSWER_PHASE_DURATION = 2.minutes
	override val PEER_PHASE_DURATION = 3.5.minutes
	override val REVISE_PHASE_DURATION = 0.5.minutes
	override val FEEDBACK_PHASE_DURATION = 1.5.minutes
}

@SciconumTechDebt
object Debate : ScenarioConstants {
	override val ANSWER_PHASE_DURATION = 1.minutes
	override val PEER_PHASE_DURATION = 5.5.minutes
	override val REVISE_PHASE_DURATION = 0.5.minutes
	override val FEEDBACK_PHASE_DURATION = 0.5.minutes
}
