export default defineNuxtRouteMiddleware(async (to) => {
	const uuid = to.params.uuid as string

	const { $api, $i18n } = useNuxtApp()
	const isSequenceOwner = await $api(
		`/v1/player/org.elaastix.engine.isSequenceOwner`,
		{
			method: 'POST',
			query: {
				sequenceUuid: uuid,
			},
		},
	)

	if (isSequenceOwner === false) {
		return showError({
			status: 403,
			statusText: $i18n.t('sequence.error.forbidden'),
		})
	}
})
