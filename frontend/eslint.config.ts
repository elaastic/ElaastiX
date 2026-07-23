import storybook from 'eslint-plugin-storybook'
import withNuxt from './.nuxt/eslint.config.mjs'

export default withNuxt([
	...(storybook.configs['flat/recommended'] as never), // as never needed, TypeScript mad otherwise :/
	{
		rules: {
			'vue/no-multiple-template-root': 'off',
			'import/first': 'off', // https://github.com/vuejs/eslint-plugin-vue/issues/1577
		},
	},
	{
		files: ['**/components/**/*.vue'],
		rules: {
			'vue/multi-word-component-names': [
				'error',
				{
					ignores: ['Sequence'],
				},
			],
		},
	},
])
