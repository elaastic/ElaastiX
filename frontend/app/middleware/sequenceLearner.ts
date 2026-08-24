export default defineNuxtRouteMiddleware(async (to) => {
	const uuid = to.params.uuid as string

	const { $api, $i18n } = useNuxtApp()
	const isSequenceLearner = await $api(
		`/v1/player/org.elaastix.engine.isSequenceLearner`,
		{
			method: 'POST',
			query: {
				sequenceUuid: uuid,
			},
		},
	)

	if (isSequenceLearner === false) {
		return showError({
			status: 403,
			statusText: $i18n.t('sequence.error.forbidden'),
		})
	}
})
