<!--
  - Elaastic / ElaastiX - formative assessment system
  - Copyright (C) 2019  Université de Toulouse and Université Toulouse Capitole.
  - SPDX-License-Identifier: AGPL-3.0-or-later
  -
  - This program is free software: you can redistribute it and/or modify
  - it under the terms of the GNU Affero General Public License as published by
  - the Free Software Foundation, either version 3 of the License, or
  - (at your option) any later version.
  -
  - This program is distributed in the hope that it will be useful,
  - but WITHOUT ANY WARRANTY; without even the implied warranty of
  - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  - GNU Affero General Public License for more details.
  -
  - You should have received a copy of the GNU Affero General Public License
  - along with this program.  If not, see <http://www.gnu.org/licenses/>.
  -->

<script lang="ts" setup>
import { useTimer } from '~/composables/timer'

const { uuid } = defineProps<{ uuid: string }>()

const {
	startSequence,
	pauseSequence,
	resumeSequence,
	data: sequenceData, // TODO: Clarify name & type
	isPending,
	isError,
	isOwner,
	duration,
	error,
	state,
} = useSequence(uuid)

const lastPage = window.history.state.back as string

const currentRound = computed(() => sequenceData.value?.currentRound ?? 0)
const question = computed(
	() =>
		sequenceData.value?.sequence.sciconumQuestions[currentRound.value]
			?.statement.content ?? '',
)
const phase = computed(() => sequenceData.value?.phase)

/* Time at which the current phase will end */
const endTime = computed(() => duration.value ? Temporal.Now.instant().add(duration.value) : undefined)

const {
	remainingTime,
	runningOutOfTime,
} = useTimer({ state, endTime })

const remainingTimeMessage = computed(() => {
	const time = remainingTime.value
	if (!time) return undefined

	return [
		{ value: time.days, unit: 'd' },
		{ value: time.hours, unit: 'h' },
		{ value: time.minutes, unit: 'm' },
		{ value: Math.floor(time.seconds), unit: 's' },
	].filter(({ value }) => value > 0)
		.map(({ value, unit }) => `${value}${unit}`)
		.join(' ') || '0s'
})
</script>

<template>
	<USkeleton
		v-if="isPending"
		class="w-full h-1/4"
	/>
	<UError
		v-else-if="isError"
		:redirect="lastPage"
		class="w-full h-full"
		icon="i-lucide-circle-x"
		:error="error"
	/>
	<UPageCard
		v-else
		class="w-full h-min"
		:highlight="runningOutOfTime"
		highlight-color="error"
	>
		<div class="flex justify-between">
			<div class="flex flex-col gap-2">
				<div class="text-xl">
					{{ sequenceData?.sequence?.name }}
				</div>
				<div
					v-if="remainingTimeMessage"
					class="text-sm text-muted -mt-2"
				>
					{{ $t("sequence.remaining") }}
					{{ remainingTimeMessage }}
				</div>
				<div>{{ question }}</div>
			</div>
			<div class="flex flex-col items-center gap-1">
				<div>{{ phase }}</div>
				<template v-if="isOwner">
					<!-- TODO: We need to update the server so that the state is never undefined -->
					<UButton
						v-if="state === 'PENDING' || state === undefined"
						icon="i-lucide-play"
						@click="startSequence"
					>
						{{ state }}
					</UButton>
					<UButton
						v-if="state === 'RUNNING'"
						icon="i-lucide-pause"
						@click="pauseSequence"
					>
						{{ state }}
					</UButton>
					<UButton
						v-if="state === 'PAUSED'"
						icon="i-lucide-play"
						@click="resumeSequence"
					>
						{{ state }}
					</UButton>
					<UButton v-if="state === 'END'">
						{{ state }}
					</UButton>
				</template>
				<div v-else>
					{{ state }}
				</div>
			</div>
		</div>
	</UPageCard>
</template>
