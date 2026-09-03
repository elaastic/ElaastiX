<script setup lang="ts">
import { State } from '~/lib/ScenarioTransitionMessage'

/**
 * This component displays the general state of a sequence.
 * For now, it is shared between Learners and Teachers.
 */
interface Props {
	/* The name of the sequence. */
	name: string

	/* The sequence state */
	state: State | undefined

	/* A string representing the current phase of the sequence. This is temporary. */
	phase: string | undefined

	/**
	 *  The total time in seconds allocated to the sequence
	 *  TODO: To be generalized to handle non-timeboxed sequences
	 */
	timeTotal: number

	/* Number of seconds elapsed for the sequence */
	timeElapsed: number

	/* Whether the sequence is considered running out of time */
	isRunningOutOfTime: boolean
}

const { state, isRunningOutOfTime, timeTotal, timeElapsed } = defineProps<Props>()
const { t, locale } = useI18n()

const remainingTimeColor = computed(() => {
	if (state === State.PAUSED) return 'secondary'
	if (isRunningOutOfTime) return 'error'
	return 'neutral'
})

const secondsRemaining = computed(() => Math.max(0, Math.ceil(timeTotal - timeElapsed)))
const durationFormatter = computed(() => new Intl.DurationFormat(locale.value.replace('_', '-'), { style: 'short' }))
const remainingMessage = computed(() => {
	const seconds = secondsRemaining.value
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
			{{ name }}
		</h2>
		<div class="flex items-center gap-2 justify-between">
			<UBadge
				v-if="state !== undefined && state !== State.END"
				size="md"
				:color="remainingTimeColor"
				variant="subtle"
			>
				{{ remainingMessage }}
			</UBadge>
			<p>{{ phase }}</p>
		</div>
	</div>
	<UProgress
		:model-value="timeElapsed"
		:max="timeTotal"
		:color="remainingTimeColor"
		class="mt-2 mb-2"
	/>
</template>
