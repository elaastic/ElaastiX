import type { Preview } from '@storybook-vue/nuxt'

const preview: Preview = {
	parameters: {
		controls: {
			matchers: {
				color: /(background|color)$/i,
				date: /Date$/i,
			},
		},
	},
}

export default preview

export const decorators = [
	story => ({
		components: { story },
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
	}),
]
