export function useSequences() {
	const { data, pending, error } = useApi(
		'/v1/player/org.elaastix.engine.getAllSciconumSequenceSession',
		{
			method: 'POST',
		},
	)

	const isError = computed(() => error !== undefined)

	return {
		data,
		isPending: pending,
		error,
		isError,
	}
}
