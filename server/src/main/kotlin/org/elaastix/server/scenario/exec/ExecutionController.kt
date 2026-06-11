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

package org.elaastix.server.scenario.exec

import org.elaastix.commons.data.Uuid
import org.elaastix.commons.platform.debt.SciconumTechDebt
import org.elaastix.server.core.player.PlayerAction
import org.elaastix.server.core.player.PlayerController
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus

@PlayerController
@SciconumTechDebt
class ExecutionController(private val scenarioExecutionService: ScenarioExecutionService) {
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PlayerAction("org.elaastix.platform.startSciconumSequence")
	fun startSciconumSequence(@RequestParam sequenceId: Uuid) {
		scenarioExecutionService.startSequenceById(sequenceId)
	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PlayerAction("org.elaastix.platform.pauseSciconumSequence")
	fun pauseSciconumSequence(@RequestParam sequenceId: Uuid) {
		scenarioExecutionService.pauseSequenceById(sequenceId)
	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PlayerAction("org.elaastix.platform.resumeSciconumSequence")
	fun resumeSciconumSequence(@RequestParam sequenceId: Uuid) {
		scenarioExecutionService.resumeSequenceById(sequenceId)
	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PlayerAction("org.elaastix.platform.resetSciconumSequence")
	fun resetSciconumSequence(@RequestParam sequenceId: Uuid) {
		scenarioExecutionService.resetSequenceById(sequenceId)
	}
}
