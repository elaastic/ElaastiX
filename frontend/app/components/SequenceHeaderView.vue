<script setup lang="ts">
import { State } from '~/lib/ScenarioTransitionMessage'

interface Props {
	name: string
	state: State | undefined
	phase: string | undefined
	totalTime: number
	timeSpend: number
	lastingTimeString: string
	lessThan10secForCurrentSeq: boolean
}

const { state, lessThan10secForCurrentSeq } = defineProps<Props>()

const badgeColor = computed(() => {
	if (state === State.PAUSED) return 'secondary'
	if (lessThan10secForCurrentSeq) return 'error'
	return 'neutral'
})

const progressColor = computed(() => {
	if (state === State.PAUSED) return 'secondary'
	if (lessThan10secForCurrentSeq) return 'error'
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
				{{ lastingTimeString }}
			</UBadge>
			<div v-else />
			<p>{{ phase }}</p>
		</div>
	</div>
	<UProgress
		:model-value="timeSpend"
		:max="totalTime"
		:color="progressColor"
		class="mt-2 mb-2"
	/>
</template>
