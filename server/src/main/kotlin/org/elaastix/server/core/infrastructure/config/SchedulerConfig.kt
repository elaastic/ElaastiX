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

package org.elaastix.server.core.infrastructure.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.time.Clock
import kotlin.time.toJavaInstant
import java.time.Clock as JClock
import java.time.Instant as JInstant

/**
 * Configuration class providing beans required for task scheduling.
 *
 * Also provides an instance of [Clock] (and [JClock] for compatibility), to make time a dependency-injected resource.
 * In tests, it is overridden with a controllable clock for easier testing of time-related events.
 */
@Configuration
@EnableAsync
@EnableScheduling
class SchedulerConfig {
	@Bean
	fun kClock(): Clock = Clock.System

	@Bean
	fun jClock(kClock: Clock): JClock = JavaClock(kClock, ZoneOffset.UTC)

	@Bean
	fun scheduler() = ThreadPoolTaskScheduler()

	private class JavaClock(private val source: Clock, private val zone: ZoneId) : JClock() {
		override fun getZone() = zone
		override fun withZone(zone: ZoneId): JClock = JavaClock(source, zone)
		override fun instant(): JInstant = source.now().toJavaInstant()

		override fun equals(obj: Any?) = obj is JavaClock && source == obj.source && zone == obj.zone
		override fun hashCode() = source.hashCode() * 31 + zone.hashCode()
		override fun toString() = "JavaClock[$zone]"
	}
}
