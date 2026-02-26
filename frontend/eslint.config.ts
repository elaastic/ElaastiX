// @ts-expect-error -- TS support in ESLint is new and the ecosystem not entirely onboard.
import storybook from 'eslint-plugin-storybook'
import withNuxt from './.nuxt/eslint.config.mjs'

export default withNuxt([
  ...storybook.configs['flat/recommended'],
])
