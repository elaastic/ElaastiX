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

import { ref, computed, openBlock, createElementBlock } from 'vue'
import { createMarkdownExit } from 'markdown-exit'

// Can't use precompiled grammars yet.
// https://github.com/shikijs/shiki/issues/918
import { bundledLanguagesInfo } from 'shiki/bundle/full'
import { createHighlighterCore } from 'shiki/core'
import { createJavaScriptRegexEngine } from 'shiki/engine/javascript'
import { fromHighlighter } from '@shikijs/markdown-exit/core'
import {
	transformerNotationDiff,
	transformerNotationHighlight,
	transformerNotationWordHighlight,
	transformerNotationFocus,
	transformerNotationErrorLevel,
	transformerRenderIndentGuides,
	transformerRemoveLineBreak,
	transformerRemoveNotationEscape,
} from '@shikijs/transformers'

import Abbr from 'markdown-it-abbr'
import Anchor from 'markdown-it-anchor'
import Footnote from 'markdown-it-footnote'
import Callouts from 'markdown-it-github-alerts'
import Mark from 'markdown-it-mark'
import Math from 'markdown-it-math/no-default-renderer'
import Sub from 'markdown-it-sub'
import Sup from 'markdown-it-sup'

import Temml from 'temml'

import 'temml/contrib/copy-tex/copy-tex.js'

import 'temml/dist/Temml-Latin-Modern.css'
import 'markdown-it-github-alerts/styles/github-colors-light.css'
import 'markdown-it-github-alerts/styles/github-colors-dark-media.css'
import 'markdown-it-github-alerts/styles/github-base.css'
import './markdown.css'

const shiki = await createHighlighterCore({
	engine: createJavaScriptRegexEngine(),
	themes: [
		import('@shikijs/themes/github-dark'),
		import('@shikijs/themes/github-light'),
	],
	langs: bundledLanguagesInfo.map(i => i.import),
})

const shikiExit = fromHighlighter(shiki, {
	themes: { dark: 'github-dark', light: 'github-light' },
	transformers: [
		transformerNotationDiff(),
		transformerNotationHighlight(),
		transformerNotationWordHighlight(),
		transformerNotationFocus(),
		transformerNotationErrorLevel(),
		transformerRenderIndentGuides({ indent: 4 }),
		transformerRemoveLineBreak(),
		transformerRemoveNotationEscape(),
	],
})

let macros = {}

// why as never: https://github.com/serkodev/markdown-exit/issues/30
const md = createMarkdownExit({ linkify: true, typographer: true })
	.use(shikiExit)
	.use(Math as never, {
		inlineRenderer: (src: string) =>
			Temml.renderToString(src, { get macros() { return macros }, annotate: true }),
		blockRenderer: (src: string) =>
			Temml.renderToString(src, { get macros() { return macros }, annotate: true, displayMode: true }),
	})
	.use(Abbr)
	.use(Anchor as never)
	.use(Footnote as never)
	.use(Callouts as never)
	.use(Mark)
	.use(Sub)
	.use(Sup)

export interface MarkdownProps {
	/** The markdown string to render. */
	markdown: string

	/** Whether to only render inline markup or not. Defaults to false. */
	inline?: boolean
}

export default defineComponent({
	__name: 'Markdown',
	__file: 'app/components/Markdown.async.ts',
	props: {
		inline: { type: Boolean },
		markdown: {},
	},
	setup(props: MarkdownProps) {
		const tag = computed(() => props.inline ? 'span' : 'div')
		const html = ref<string>()
		const el = ref<HTMLElement>()

		watchEffect(async (onCleanup) => {
			let cancelled = false
			onCleanup(() => (cancelled = true))

			try {
				const rendered = await (props.inline ? md.renderInlineAsync(props.markdown) : md.renderAsync(props.markdown))
				if (!cancelled) html.value = rendered
			} finally {
				macros = {}
			}
		})

		// `postProcess` implements `\ref` and `\eqref`
		watch([html, el], () => html.value && el.value && Temml.postProcess(el.value), { flush: 'post' })

		return () => {
			return (
				openBlock(),
				createElementBlock(
					tag.value,
					{
						innerHTML: html.value,
						class: 'markdown-prose',
						ref_key: 'el',
						ref: el,
					},
					null,
					8 /* PROPS */,
					['innerHTML'],
				)
			)
		}
	},
})
