import type { StorybookConfig } from '@storybook-vue/nuxt'
import { realpathSync } from 'node:fs'
import { createRequire } from 'node:module'

const _require = createRequire(import.meta.url)

// Resolve the real filesystem path for vue/dist/vue.esm-bundler.js once at startup.
// We must resolve to the esm-bundler entry directly — resolving to vue/index.js
// would re-import vue.esm-bundler.js and loop right back.
let _realVuePath: string | undefined
function getRealVuePath(): string | undefined {
	if (_realVuePath === undefined) {
		try {
			_realVuePath = realpathSync(_require.resolve('vue/dist/vue.esm-bundler.js'))
		} catch {
			_realVuePath = ''
		}
	}
	return _realVuePath || undefined
}

const config: StorybookConfig = {
	stories: [
		'../app/components/**/*.mdx',
		'../app/components/**/*.stories.@(js|jsx|ts|tsx|mdx)',
	],
	addons: [
		'@storybook/addon-a11y',
		'@storybook/addon-docs',
	],
	framework: '@storybook-vue/nuxt',
	async viteFinal(config) {
		const realVuePath = getRealVuePath()

		config.plugins = config.plugins || []
		config.plugins.unshift({
			name: 'pnpm-vue-resolve-guard',
			enforce: 'pre',
			resolveId(id) {
				// Break infinite resolveId recursion on vue paths under pnpm symlinks.
				// Pattern: vue/dist/vue.esm-bundler.js/dist/vue.esm-bundler.js/dist/...
				if (id === 'vue' || id.includes('/vue/dist/') || id.includes('vue.esm-bundler')) {
					if (!realVuePath || id === realVuePath) {
						return null
					}
					return { id: realVuePath, external: false }
				}
			},
		})

		// Force all bare `vue` imports to the ESM bundler entry (real path).
		// Without this, Vite's optimizer may resolve to vue/index.js (CJS, no named exports).
		if (realVuePath) {
			config.resolve = config.resolve || {}
			config.resolve.alias = {
				...(typeof config.resolve.alias === 'object' && !Array.isArray(config.resolve.alias)
					? config.resolve.alias
					: {}),
				vue: realVuePath,
			}
		}

		// Vite 8 defaults to lightningcss for CSS minification, which chokes on
		// some Tailwind v3 pseudo-class selectors (e.g. before:placeholder-*).
		// Fall back to esbuild until Tailwind v4 or a lightningcss fix lands.
		config.build = config.build || {}
		config.build.cssMinify = 'esbuild'

		return config
	},
}

export default config
