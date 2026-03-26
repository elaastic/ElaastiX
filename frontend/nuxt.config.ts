/*
 * Elaastic / ElaastiX - formative assessment system
 * Copyright (C) 2019  Université de Toulouse and Université Toulouse Capitole.
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

export default defineNuxtConfig({
	modules: [
		'@nuxt/eslint',
		'@nuxt/ui',
		'@nuxt/test-utils/module',
		'@nuxt/image',
		'@nuxt/icon',
		'@nuxt/fonts',
		// '@nuxt/hints',
		'@nuxt/a11y',
		'@nuxtjs/i18n',
		// '@nuxtjs/storybook',
	],

	devtools: { enabled: true },

	css: ['~/assets/css/main.css'],

	runtimeConfig: {
		public: {
			version: 'v0-indev',
			gitHash: 'deadbeef',
		},
	},

	routeRules: {},

	compatibilityDate: '2025-07-15',

	nitro: {
		devProxy: {
			'/api': { target: 'http://localhost:8080', prependPath: false },
		},
	},

	eslint: {
		config: {
			stylistic: {
				indent: 'tab',
				commaDangle: 'always-multiline',
				braceStyle: '1tbs',
			},
		},
	},

	i18n: {
		defaultLocale: 'en',
		locales: [
			{ code: 'en', name: 'English', language: 'en-GB', file: 'en.json' },
			{ code: 'fr', name: 'Français', language: 'fr-FR', file: 'fr.json' },
		],
		strategy: 'no_prefix',
	},
})
