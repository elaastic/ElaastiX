const { user } = useAuthn()

export function useSequences() {
	const { data, pending, error } = useApi(
		'/v1/player/org.elaastix.engine.getAllSciconumSequenceSessionAssociated',
		{
			method: 'POST',
		},
	)

	const isError = computed(() => error.value !== undefined)
	const sequences = computed(() => {
		if (!data.value) return undefined

		const rawData = data.value

		return rawData.map(e => ({
			...e,
			isOwner: user.value?.id === e.sequence.ownerId,
		}))
	})

	return {
		sequences,
		isPending: pending,
		error,
		isError,
	}
}
