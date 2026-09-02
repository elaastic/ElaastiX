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
import type { State } from '~/lib/ScenarioTransitionMessage'

interface UseTimerOptions {
	state: MaybeRefOrGetter<State | undefined>
	endTime: MaybeRefOrGetter<Temporal.Instant | undefined>
	refreshInterval?: number
	runningOutOfTimeThreshold?: number
}

/**
 * Timer composable
 * @param state The state of this timer.
 * @param endTime The instant at which the timer should stop. It may be undefined. It may be a value of a ref.
 * @param refreshInterval The interval at which the timer should refresh.
 * @param runningOutOfTimeThreshold The threshold at which the timer should be considered as running out of time (in seconds).
 */
export const useTimer = ({ state, endTime, refreshInterval = 500, runningOutOfTimeThreshold = 10 }: UseTimerOptions) => {
	const tick = ref(0)

	const remainingTime = computed(() => {
		void tick.value // Register tick as a dependency

		const now = Temporal.Now.instant()
		const endTimeValue = toValue(endTime)
		const stateValue = toValue(state)

		if (stateValue === 'END') {
			return Temporal.Duration.from({ seconds: 0 })
		}

		if (endTimeValue === undefined) {
			return undefined
		}

		return Temporal.Instant.compare(now, endTimeValue) < 0
			? now.until(endTimeValue)
			: Temporal.Duration.from({ seconds: 0 })
	})

	const runningOutOfTime = computed(
		() => !!remainingTime.value && remainingTime.value.total('milliseconds') < runningOutOfTimeThreshold * 1000,
	)

	let interval: ReturnType<typeof setInterval> | undefined

	onMounted(() => {
		interval = setInterval(() => {
			if (toValue(state) === 'RUNNING') {
				tick.value++
			}
		}, refreshInterval)
	})

	onUnmounted(() => {
		if (interval !== undefined) {
			clearInterval(interval)
		}
	})

	return {
		/* The current remaining time */
		remainingTime,

		/* True if the timer is running out of time */
		runningOutOfTime,
	}
}
