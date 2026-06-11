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

import org.elaastix.commons.platform.debt.SciconumTechDebt
import org.elaastix.server.core.infrastructure.ExcludeFromSyntheticBoot
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

/**
 * Service implementing the execution flow of the sequences.
 * Caution: it assumes there is only one instance of the app ever running (which is going to be the case for now).
 */
@Component
@ExcludeFromSyntheticBoot
@SciconumTechDebt
class ScenarioExecutionRestoreRunner(private val executionService: ScenarioExecutionService) : CommandLineRunner {
	override fun run(vararg args: String) {
		executionService.restoreRunningSequences()
	}
}
