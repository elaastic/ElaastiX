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
import org.elaastix.server.core.player.PlayerSystemController
import org.elaastix.server.scenario.exec.dto.SciconumScenarioPhaseDto
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * Engine-level operations controlling the execution of a sequence session.
 */
@PlayerSystemController
@SciconumTechDebt
class ExecutionController(private val scenarioExecutionService: ScenarioExecutionService) {

	@PlayerAction("org.elaastix.engine.getSciconumSequenceSession")
	fun getSciconumSequenceSession(@RequestParam scenarioSessionId: String): SciconumScenarioPhaseDto =
		scenarioExecutionService.getSciconumScenarioStateById(Uuid.parse(scenarioSessionId))

	/**
	 * Start a SCICONUM sequence session.
	 *
	 * @see [ScenarioExecutionService.startSequenceScenarioSessionById]
	 */
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PlayerAction("org.elaastix.engine.startSciconumScenarioSession")
	fun startSciconumSequenceSession(@RequestParam scenarioSessionId: String) {
		scenarioExecutionService.startSequenceScenarioSessionById(Uuid.parse(scenarioSessionId))
	}

	/**
	 * Pause a SCICONUM sequence session.
	 *
	 * @see [ScenarioExecutionService.pauseSequenceScenarioSessionById]
	 */
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PlayerAction("org.elaastix.engine.pauseSciconumScenarioSession")
	fun pauseSciconumSequenceSession(@RequestParam scenarioSessionId: String) {
		scenarioExecutionService.pauseSequenceScenarioSessionById(Uuid.parse(scenarioSessionId))
	}

	/**
	 * Start a SCICONUM sequence session.
	 *
	 * @see [ScenarioExecutionService.resumeSequenceScenarioSessionById]
	 */
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PlayerAction("org.elaastix.engine.resumeSciconumScenarioSession")
	fun resumeSciconumSequenceSession(@RequestParam scenarioSessionId: String) {
		scenarioExecutionService.resumeSequenceScenarioSessionById(Uuid.parse(scenarioSessionId))
	}

	/**
	 * Start a SCICONUM sequence session.
	 *
	 * @see [ScenarioExecutionService.resetSequenceScenarioSessionById]
	 */
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PlayerAction("org.elaastix.engine.resetSciconumScenarioSession")
	fun resetSciconumSequenceSession(@RequestParam scenarioSessionId: String) {
		scenarioExecutionService.resetSequenceScenarioSessionById(Uuid.parse(scenarioSessionId))
	}
}
