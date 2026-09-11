import type { Preview } from '@storybook-vue/nuxt'
import { ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'

type StoryLocale = 'fr_fr' | 'en_gb'

// Storybook can rerun decorators without remounting their Vue components.
const selectedLocale = ref<StoryLocale>('fr_fr')

const preview: Preview = {
	globalTypes: {
		locale: {
			description: 'Language of the stories',
			toolbar: {
				icon: 'globe',
				items: [
					{ value: 'fr', title: 'Français' },
					{ value: 'en', title: 'English' },
				],
				dynamicTitle: true,
			},
		},
	},
	initialGlobals: {
		locale: 'fr',
	},
	parameters: {
		controls: {
			matchers: {
				color: /(background|color)$/i,
				date: /Date$/i,
			},
		},
	},
	decorators: [
		(story, context) => {
			selectedLocale.value = context.globals.locale === 'en' ? 'en_gb' : 'fr_fr'
			return {
				components: { story },
				setup() {
					const { locale, loadLocaleMessages } = useI18n({ useScope: 'global' })
					watch(selectedLocale, async (value, _previous, onCleanup) => {
						let cancelled = false
						onCleanup(() => {
							cancelled = true
						})
						await loadLocaleMessages(value)
						if (!cancelled) locale.value = value
					}, { immediate: true })
				},
				template: `
					<suspense>
						<template #default>
							<story />
						</template>
						<template #fallback>
							<div>Loading...</div>
						</template>
					</suspense>
				`,
			}
		},
	],
}

export default preview
