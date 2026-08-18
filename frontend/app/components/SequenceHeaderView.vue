<script setup lang="ts">
import { State } from '~/lib/ScenarioTransitionMessage'

interface Props {
	name: string
	state: State | undefined
	phase: string | undefined
	lastingTime: string
	lessThan10secForCurrentSeq: boolean
}

const { state, lessThan10secForCurrentSeq } = defineProps<Props>()

const color = computed(() => {
	if (state === State.PAUSED) return 'secondary'
	if (lessThan10secForCurrentSeq) return 'error'
	return 'neutral'
})
</script>

<template>
	<div class="flex justify-between">
		<h2 class="text-2xl">
			{{ name }}
		</h2>
		<div class="flex items-center gap-2 w-1/6 justify-between">
			<UBadge
				v-if="state !== undefined && state !== State.END"
				size="md"
				:color="color"
				variant="subtle"
			>
				{{ lastingTime }}
			</UBadge>
			<div v-else />
			<p>{{ phase }}</p>
		</div>
	</div>
</template>
