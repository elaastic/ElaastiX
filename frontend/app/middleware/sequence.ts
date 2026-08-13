export default defineNuxtRouteMiddleware(async (to) => {
	const uuid = to.params.uuid as string

	const { $api, $i18n } = useNuxtApp()
	const data = await $api(
		`/v1/player/org.elaastix.engine.getSciconumSequenceSession`,
		{
			method: 'POST',
			query: {
				scenarioSessionId: uuid,
			},
		},
	)

	const { user: owner } = useAuthn()

	if (data?.sequence.ownerId !== owner.value?.id) {
		return showError({
			status: 403,
			statusText: $i18n.t('sequence.error.forbidden'),
		})
	}
})
