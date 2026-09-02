export function useSequences() {
	const { data, pending, error } = useApi(
		'/v1/player/org.elaastix.engine.getAllSciconumSequenceSessionAssociated',
		{
			method: 'POST',
		},
	)

	const isError = computed(() => error !== undefined)

	return {
		sequences: data,
		isPending: pending,
		error,
		isError,
	}
}
