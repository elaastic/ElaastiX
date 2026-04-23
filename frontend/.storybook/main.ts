import type { StorybookConfig } from '@storybook-vue/nuxt'

const config: StorybookConfig = {
	stories: [
		'../app/**/*.stories.@(ts|tsx|mdx)',
	],
	addons: [
		'@storybook/addon-a11y',
		'@storybook/addon-docs',
		'storybook-i18n',
	],
	framework: {
		name: '@storybook-vue/nuxt',
		options: {
			docgen: {
				plugin: 'vue-component-meta',
				// @ts-expect-error -- Type is excessively restrictive here :/
				tsconfig: '.nuxt/tsconfig.app.json',
			},
		},
	},
	features: {
		backgrounds: false,
	},
}

export default config
