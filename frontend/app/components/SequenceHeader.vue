<script setup lang="ts">
import { State } from '~/lib/ScenarioTransitionMessage'
import type { SequenceExecution } from '~/types/sequence'

/**
 * This component displays the general state of a sequence.
 * For now, it is shared between Learners and Teachers.
 */
interface Props {
	sequenceExecution: SequenceExecution
}

const { sequenceExecution } = defineProps<Props>()
const { t, locale } = useI18n()

const remainingTimeColor = computed(() => {
	if (sequenceExecution.state === State.PAUSED) return 'secondary'
	if (sequenceExecution.timebox?.isRunningOutOfTime) return 'error'
	return 'neutral'
})

const secondsRemaining = computed(() => sequenceExecution.timebox ? Math.max(0, Math.ceil(sequenceExecution.timebox.timeTotal - sequenceExecution.timebox.timeElapsed)) : undefined)
const durationFormatter = computed(() => new Intl.DurationFormat(locale.value.replace('_', '-'), { style: 'short' }))
const remainingMessage = computed(() => {
	const seconds = secondsRemaining.value
	if (seconds === undefined) return undefined
	if (seconds === 0) return t('sequence.timeElapsed')

	const duration = durationFormatter.value.format({
		days: Math.floor(seconds / 86400),
		hours: Math.floor(seconds / 3600) % 24,
		minutes: Math.floor(seconds / 60) % 60,
		seconds: seconds % 60,
	})
	return t('sequence.remainingTime', { duration })
})
</script>

<template>
	<div class="flex justify-between">
		<h2 class="text-2xl">
			{{ sequenceExecution.name }}
		</h2>
		<div class="flex items-center gap-2 justify-between">
			<UBadge
				v-if="sequenceExecution.timebox && sequenceExecution.state !== undefined && sequenceExecution.state !== State.END"
				size="md"
				:color="remainingTimeColor"
				variant="subtle"
			>
				{{ remainingMessage }}
			</UBadge>
			<p>{{ sequenceExecution.phase }}</p>
		</div>
	</div>
	<UProgress
		v-if="sequenceExecution.timebox"
		:model-value="sequenceExecution.timebox.timeElapsed"
		:max="sequenceExecution.timebox.timeTotal"
		:color="remainingTimeColor"
		class="mt-2 mb-2"
	/>
</template>
