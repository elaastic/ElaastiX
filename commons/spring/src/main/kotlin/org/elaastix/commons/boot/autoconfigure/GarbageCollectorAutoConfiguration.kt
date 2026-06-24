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

package org.elaastix.commons.boot.autoconfigure

import org.elaastix.commons.gc.GarbageCollector
import org.elaastix.commons.platform.ExcludeFromSyntheticBoot
import org.elaastix.commons.scheduleWithFixedDelay
import org.springframework.beans.factory.getBeansOfType
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Import
import org.springframework.scheduling.TaskScheduler
import kotlin.time.Duration.Companion.hours

/**
 * Autoconfiguration class setting up the garbage collection task.
 *
 * @see [org.elaastix.commons.gc.GarbageCollector]
 */
@AutoConfiguration
@Import(GarbageCollectorAutoConfiguration.GCCommandRunner::class)
class GarbageCollectorAutoConfiguration {
	/**
	 * Runner scheduling garbage collection tasks.
	 */
	@ExcludeFromSyntheticBoot
	class GCCommandRunner(private val applicationContext: ApplicationContext, private val taskScheduler: TaskScheduler) :
		CommandLineRunner {
		override fun run(vararg args: String) {
			taskScheduler.scheduleWithFixedDelay(
				{ applicationContext.getBeansOfType<GarbageCollector>().values.forEach { it.gc() } },
				1.hours,
			)
		}
	}
}
