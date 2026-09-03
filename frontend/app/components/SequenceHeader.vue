<script setup lang="ts">
import { State } from '~/lib/ScenarioTransitionMessage'

interface Props {
	name: string
	state: State | undefined
	phase: string | undefined
	timeTotal: number // in seconds
	timeElapsed: number // in seconds
	isRunningOutOfTime: boolean
}

const { state, isRunningOutOfTime, timeTotal, timeElapsed } = defineProps<Props>()

const badgeColor = computed(() => {
	if (state === State.PAUSED) return 'secondary'
	if (isRunningOutOfTime) return 'error'
	return 'neutral'
})

const timeRemaining = computed(() => Math.max(0, timeTotal - timeElapsed))
const { remainingMessage } = useDurationMessage(timeRemaining)

const progressColor = computed(() => {
	if (state === State.PAUSED) return 'secondary'
	if (isRunningOutOfTime) return 'error'
	return 'primary'
})
</script>

<template>
	<div class="flex justify-between">
		<h2 class="text-2xl">
			{{ name }}
		</h2>
		<div class="flex items-center gap-2 justify-between">
			<UBadge
				v-if="state !== undefined && state !== State.END"
				size="md"
				:color="badgeColor"
				variant="subtle"
			>
				{{ remainingMessage }}
			</UBadge>
			<div v-else />
			<p>{{ phase }}</p>
		</div>
	</div>
	<UProgress
		:model-value="timeElapsed"
		:max="timeTotal"
		:color="progressColor"
		class="mt-2 mb-2"
	/>
</template>
