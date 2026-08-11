import type { State } from '~/lib/ScenarioTransitionMessage'

export function useSequence(uuid: string) {
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
	const duration = ref<string | undefined | null>(undefined)

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
			}
		},
	})

	return {
		startSequence,
		pauseSequence,
		resumeSequence,
		data,
		isPending,
		isError,
		error,
		state,
		duration,
	}
}
