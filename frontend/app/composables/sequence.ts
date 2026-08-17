import type { State } from '~/lib/ScenarioTransitionMessage'

function getRemainingTime(
	duration: Ref<Temporal.Duration | undefined | null>,
	state: Ref<State | undefined>,
) {
	const lastingTime = ref('')
	const lessThan10secForCurrentSeq = ref(false)

	const timeZone = Temporal.Now.timeZoneId()

	const now = ref<undefined | Temporal.Instant>()
	const endingTime = ref<undefined | Temporal.ZonedDateTime>()

	const interval = setInterval(() => {
		if (duration.value === undefined || duration.value === null) return
		if (endingTime.value === undefined || endingTime.value === null) return

		const currentTime = Temporal.Now.zonedDateTimeISO(timeZone)

		if (
			Temporal.ZonedDateTime.compare(currentTime, endingTime.value) >= 0
		) {
			return
		}

		if (state.value === 'PAUSED') {
			return
		}

		const remaining = endingTime.value.since(currentTime, {
			largestUnit: 'day',
		})

		const parts = []
		if (remaining.days > 0) parts.push(`${remaining.days}d`)
		if (remaining.hours > 0) parts.push(`${remaining.hours}h`)
		if (remaining.minutes > 0) parts.push(`${remaining.minutes}m`)
		if (remaining.seconds > 0)
			parts.push(`${Math.floor(remaining.seconds)}s`)

		lastingTime.value = parts.join(' ') || '0s'

		lessThan10secForCurrentSeq.value
			= remaining.days === 0
				&& remaining.hours === 0
				&& remaining.minutes === 0
				&& remaining.seconds <= 10
				&& remaining.seconds !== 0
	}, 500)

	watch(duration, () => {
		if (state.value === 'END') {
			clearInterval(interval)
			lastingTime.value = ''
			lessThan10secForCurrentSeq.value = false
			return
		}

		now.value = Temporal.Now.instant()

		endingTime.value = now.value
			.add(duration.value!)
			.toZonedDateTimeISO(timeZone)
	})

	return { lastingTime, lessThan10secForCurrentSeq }
}

export function useSequence(uuid: string, owner: boolean) {
	const { $api } = useNuxtApp()

	const {
		data: sequenceData,
		pending: isPending,
		error,
	} = useApi('/v1/player/org.elaastix.engine.getSciconumSequenceSession', {
		method: 'POST',
		query: {
			scenarioSessionId: uuid,
		},
	})

	const data = ref<undefined | SciconumScenarioPhaseDto>(undefined)
	const isError = computed(() => error.value !== undefined)
	const state = ref<State | undefined>(undefined)
	const duration = ref<Temporal.Duration | undefined | null>(undefined)
	const name = computed(
		() => data.value?.sequence.name ?? 'This sequence does not exists',
	)
	const currentRound = computed(() => data.value?.currentRound ?? 0)
	const question = computed(
		() => data.value?.sequence.sciconumQuestions[currentRound.value],
	)
	const phase = computed(() => data.value?.phase)

	watch(sequenceData, () => (data.value = sequenceData.value))

	const startSequence = () => {
		$api('/v1/player/org.elaastix.engine.startSciconumScenarioSession', {
			method: 'POST',
			query: {
				scenarioSessionId: uuid,
			},
		})
	}

	const pauseSequence = () => {
		$api('/v1/player/org.elaastix.engine.pauseSciconumScenarioSession', {
			method: 'POST',
			query: {
				scenarioSessionId: uuid,
			},
		})
	}

	const resumeSequence = () => {
		$api('/v1/player/org.elaastix.engine.resumeSciconumScenarioSession', {
			method: 'POST',
			query: {
				scenarioSessionId: uuid,
			},
		})
	}

	useWebSocket({
		onOpen: () => {
			console.log('The websocket opened')
		},
		onClose: () => {
			console.log('The websocket closed')
		},
		onError: () => {
			console.log('The websocket encountered an error')
		},
		onMessage: (dataReceived) => {
			console.log(
				`data received: ${JSON.stringify(dataReceived, null, 2)}`,
			)

			if (data.value !== undefined) {
				data.value.phase = dataReceived.sciconumPhase
				state.value = dataReceived.state
				duration.value = dataReceived.duration
				data.value.currentRound = dataReceived.currentRound
			}
		},
	})

	const { lastingTime, lessThan10secForCurrentSeq } = getRemainingTime(
		duration,
		state,
	)

	if (owner) {
		return {
			startSequence,
			pauseSequence,
			resumeSequence,
			data,
			isPending,
			isError,
			error,
			state,
			name,
			question,
			phase,
			duration,
			lastingTime,
			lessThan10secForCurrentSeq,
		}
	}

	return {
		data,
		isPending,
		isError,
		error,
		state,
		name,
		question,
		phase,
		duration,
		lastingTime,
		lessThan10secForCurrentSeq,
	}
}
