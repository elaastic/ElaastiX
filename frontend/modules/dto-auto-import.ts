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

import { addTypeTemplate, defineNuxtModule, resolvePath, addImportsSources } from 'nuxt/kit'
import { readFile } from 'fs/promises'

export default defineNuxtModule({
	meta: {
		name: 'dto-auto-import',
	},
	async setup (_, nuxt) {
		if (typeof nuxt.options.openFetch !== 'object') return

		const specPath = nuxt.options.openFetch.clients?.api?.schema
		if (typeof specPath !== 'string') return

		const path = await resolvePath(specPath)
		const types = await readFile(path, 'utf8')

		const dtoSet = new Set(types.matchAll(/\w+Dto/g).map((m) => m[0]))
		const dtos = [...dtoSet]

		addTypeTemplate({
			filename: 'elaastix-api-dtos.d.ts',
			getContents: () => {
				const baseImport = 'import { components } from "#open-fetch-schemas/api"'
				const exports = dtos.map((dto) => `export type ${dto} = components["schemas"]["${dto}"]`)

				return `${baseImport}\n\n${exports.join('\n')}`
			},
			write: true,
		})

		addImportsSources({
			from: `${nuxt.options.buildDir}/elaastix-api-dtos.d.ts`,
			imports: dtos,
			type: true,
		})
	},
})
