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

package org.elaastix.commons

import org.springframework.scheduling.TaskScheduler
import java.util.concurrent.ScheduledFuture
import kotlin.time.Duration
import kotlin.time.Instant
import kotlin.time.toJavaDuration
import kotlin.time.toJavaInstant

/** Kotlin-native extension of [TaskScheduler.schedule]. */
fun TaskScheduler.schedule(task: Runnable, startTime: Instant): ScheduledFuture<*> =
	schedule(task, startTime.toJavaInstant())

/** Kotlin-native extension of [TaskScheduler.scheduleAtFixedRate]. */
fun TaskScheduler.scheduleAtFixedRate(task: Runnable, startTime: Instant, period: Duration): ScheduledFuture<*> =
	scheduleAtFixedRate(task, startTime.toJavaInstant(), period.toJavaDuration())

/** Kotlin-native extension of [TaskScheduler.scheduleAtFixedRate]. */
fun TaskScheduler.scheduleAtFixedRate(task: Runnable, period: Duration): ScheduledFuture<*> =
	scheduleAtFixedRate(task, period.toJavaDuration())

/** Kotlin-native extension of [TaskScheduler.scheduleWithFixedDelay]. */
fun TaskScheduler.scheduleWithFixedDelay(task: Runnable, startTime: Instant, delay: Duration): ScheduledFuture<*> =
	scheduleWithFixedDelay(task, startTime.toJavaInstant(), delay.toJavaDuration())

/** Kotlin-native extension of [TaskScheduler.scheduleWithFixedDelay]. */
fun TaskScheduler.scheduleWithFixedDelay(task: Runnable, delay: Duration): ScheduledFuture<*> =
	scheduleWithFixedDelay(task, delay.toJavaDuration())
