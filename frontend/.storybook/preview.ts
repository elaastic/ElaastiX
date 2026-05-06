/*
 * Elaastic / ElaastiX - formative assessment system
 * Copyright (C) 2019 Université de Toulouse and Université Toulouse Capitole.
 * Copyright (c) 2026 npmx team and contributors
 * SPDX-License-Identifier: AGPL-3.0-or-later AND MIT
 *
 * Portions of this file have been taken from the npmx.dev project, licensed under the MIT license.
 * https://github.com/npmx-dev/npmx.dev/blob/189a56846b23ce7a7a73e339cfca4623dbb3d72c/.storybook/preview.ts
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

import type { Preview } from '@storybook-vue/nuxt'
import { addons } from 'storybook/preview-api'
import { locales } from '../config/i18n.js'

// Subscribe to locale changes from storybook-i18n addon (once, outside decorator)
let currentI18nInstance: any | null = null // eslint-disable-line @typescript-eslint/no-explicit-any -- easier that way
addons.getChannel().on('LOCALE_CHANGED', (newLocale: string) => {
	currentI18nInstance?.setLocale(newLocale)
})

const preview: Preview = {
	parameters: {
		controls: {
			matchers: {
				color: /(background|color)$/i,
				date: /Date$/i,
			},
		},
	},
	initialGlobals: {
		locale: 'en-US',
		locales: locales.reduce(
			(acc, locale) => {
				acc[locale.code] = locale.name!
				return acc
			},
			{} as Record<string, string>,
		),
	},
	decorators: [
		(story, context) => {
			const { locale } = context.globals as { locale?: string }

			return {
				template: '<story />',
				created() {
					// Store i18n instance for LOCALE_CHANGED events
					currentI18nInstance = this.$i18n

					// Set initial locale when component is created
					if (locale && this.$i18n) {
						this.$i18n.setLocale(locale)
					}
				},
			}
		},
	],
}

export default preview
