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

package testutils

import org.elaastix.commons.platform.Unsafe
import org.springframework.scheduling.SchedulingException
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.Trigger
import org.springframework.scheduling.support.SimpleTriggerContext
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.PriorityQueue
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Delayed
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant
import kotlin.time.toDurationUnit
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant
import java.time.Clock as JClock
import java.time.Duration as JDuration
import java.time.Instant as JInstant

/**
 * An implementation of [Clock] and [JClock] that is controllable, for use in tests.
 *
 * Instances of the clock also provide a virtual task scheduler that is aware of the faked time.
 * When time is moved forward, the clock will do a best-effort job at executing scheduled tasks "as expected".
 *
 * The clock is internally isolated per-thread, making it compatible with concurrent test execution.
 */
class ControllableClock :
	JClock(),
	Clock {
	// Could also pick a random start point...
	private val instant = ThreadLocal.withInitial { Clock.System.now() }

	val scheduler = ControllableClockAwareTaskScheduler()

	override fun now(): Instant = instant.get()

	override fun getZone(): ZoneId = ZoneOffset.UTC
	override fun withZone(zone: ZoneId): JClock = ZonedClock(this, zone)
	override fun instant() = instant.get().toJavaInstant()

	/**
	 * Moves the clock forwards in time.
	 *
	 * This operation will notify the [scheduler], which will synchronously run all tasks that are expected to run
	 * before or at `now + duration`.
	 *
	 * Scheduled tasks will be run in strict order, and will observe the exact time at which they've been scheduled on
	 * the clock. Periodic tasks may run multiple time if the increment covers multiple executions of the task.
	 *
	 * As the scheduler runs the tasks synchronously, any exception thrown in a task will fail the test. This is a
	 * strongly desirable behaviour, as this lets the test framework capture the exception and fail the test as soon as
	 * it occurs.
	 *
	 * @throws org.springframework.scheduling.SchedulingException if an exception occurs while running scheduled tasks.
	 */
	fun add(duration: Duration) {
		val targetTime = instant.get().plus(duration)
		scheduler.tick(targetTime)
		instant.set(targetTime)
	}

	/**
	 * Moves the clock backwards in time.
	 *
	 * This operation is [Unsafe], as clock rollbacks (while theoretically expected) are undesired and break
	 * the "natural" passage of time and causes ambiguity with scheduled tasks.
	 *
	 * The scheduler will not run any task, and the next execution of currently scheduled tasks will not be affected.
	 * This includes recurring scheduled tasks, which means they will only run periodically until time reaches the
	 * currently next planned execution.
	 *
	 * In other words, if a task is scheduled to run every 10 seconds, and the clock is moved back 1 minute, then the
	 * next execution of the task will be in (approximately) 70 seconds. Its second next execution will be 10 seconds
	 * after that, so in 80 seconds. Its reference point will not be moved back.
	 */
	@Unsafe
	fun sub(duration: Duration) {
		instant.set(instant.get().minus(duration))
	}

	/**
	 * Moves the clock to a specific instant. The target instant MUST be in the future.
	 *
	 * For moving the clock to an earlier [Instant], see the **unsafe** variant [moveUnchecked].
	 *
	 * @throws IllegalArgumentException if [instant] is earlier than the current time.
	 * @throws SchedulingException if an exception occurs while running scheduled tasks.
	 */
	fun move(instant: Instant) {
		// Thread safety: because the value is thread local, there's no risk of a reading a stale value.
		require(instant > this.instant.get())
		scheduler.tick(instant)
		this.instant.set(instant)
	}

	/**
	 * Moves the clock to a specific instant, **without ticking the scheduler**.
	 * Does not check whether [instant] is in the future or past either.
	 *
	 * See [sub]'s documentation for the implications of moving the clock backwards.
	 */
	@Unsafe
	fun moveUnchecked(instant: Instant) {
		this.instant.set(instant)
	}

	inner class ControllableClockAwareTaskScheduler : TaskScheduler {
		private val tasks = ThreadLocal.withInitial { PriorityQueue<Task>() }

		fun tick(targetTime: Instant) {
			val queue = tasks.get()
			while (queue.peek()?.let { it.scheduledAt <= targetTime } == true) {
				val task = queue.remove()
				if (task.isCancelled) continue

				instant.set(task.scheduledAt)
				if (!task.run()) {
					throw SchedulingException(
						"An exception occurred while running a scheduled task",
						task.exceptionNow(),
					)
				}

				val ctx = createTriggerContext(task.scheduledAt)
				task.trigger.nextExecution(ctx)
					?.let { next -> queue.add(task.rerunAt(next.toKotlinInstant())) }
					?: task.future.complete(Unit)
			}
		}

		private fun scheduleChecked(task: Runnable, trigger: Trigger) = checkNotNull(schedule(task, trigger))

		override fun schedule(task: Runnable, trigger: Trigger): ScheduledFuture<*>? {
			val ctx = createTriggerContext(null)
			val next = trigger.nextExecution(ctx)?.toKotlinInstant() ?: return null
			return Task(task, next, trigger, this@ControllableClock).also {
				tasks.get().add(it)
				tick(now())
			}
		}

		override fun schedule(task: Runnable, jStartTime: JInstant) =
			scheduleChecked(task) { if (it.lastCompletion() == null) jStartTime else null }

		override fun scheduleAtFixedRate(task: Runnable, jStartTime: JInstant, jPeriod: JDuration) =
			scheduleChecked(task) {
				val prev = it.lastActualExecution() ?: jStartTime
				prev.plus(jPeriod)
			}

		override fun scheduleWithFixedDelay(task: Runnable, jStartTime: JInstant, jDelay: JDuration) =
			scheduleChecked(task) {
				val prev = it.lastCompletion() ?: jStartTime
				prev.plus(jDelay)
			}

		override fun scheduleAtFixedRate(task: Runnable, jPeriod: JDuration) = scheduleAtFixedRate(task, instant(), jPeriod)

		override fun scheduleWithFixedDelay(task: Runnable, jDelay: JDuration) =
			scheduleWithFixedDelay(task, instant(), jDelay)

		// We completely mock the passage of time, so all 3 values of the trigger context are artificially equal:
		// - The scheduler will "perfectly" execute the task at the exact nanosecond it's supposed to
		// - The task will take zero second since we're not using wall clock
		private fun createTriggerContext(instant: Instant?) =
			SimpleTriggerContext(this@ControllableClock).apply {
				update(instant?.toJavaInstant(), instant?.toJavaInstant(), instant?.toJavaInstant())
			}
	}

	private class Task(
		val task: Runnable,
		val scheduledAt: Instant,
		val trigger: Trigger,
		val clock: Clock,
		val future: CompletableFuture<Unit> = CompletableFuture<Unit>(),
	) : ScheduledFuture<Unit> {

		fun run() =
			runCatching { task.run() }
				.onFailure { future.completeExceptionally(it) }
				.isSuccess

		fun rerunAt(next: Instant) = Task(task, next, trigger, clock, future)

		override fun cancel(mayInterruptIfRunning: Boolean): Boolean = future.cancel(mayInterruptIfRunning)
		override fun isCancelled(): Boolean = future.isCancelled()
		override fun isDone(): Boolean = future.isDone
		override fun get(): Unit = future.get()
		override fun get(timeout: Long, unit: TimeUnit): Unit = future.get(timeout, unit)

		override fun getDelay(unit: TimeUnit) = (scheduledAt - clock.now()).toLong(unit.toDurationUnit())
		override fun compareTo(other: Delayed) =
			getDelay(TimeUnit.NANOSECONDS).compareTo(other.getDelay(TimeUnit.NANOSECONDS))
	}

	private class ZonedClock(private val source: JClock, private val zone: ZoneId) : JClock() {
		override fun getZone() = zone
		override fun withZone(zone: ZoneId) = ZonedClock(source, zone)
		override fun instant(): JInstant = source.instant()

		override fun equals(obj: Any?) = obj is ZonedClock && source == obj.source && zone == obj.zone
		override fun hashCode() = source.hashCode() * 31 + zone.hashCode()
		override fun toString() = "ControllableClock#Zoned[$zone]"
	}
}
