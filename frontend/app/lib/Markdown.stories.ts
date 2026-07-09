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

import type { Meta, StoryObj } from '@nuxtjs/storybook'

import MarkdownComponent from './Markdown.async'

const meta = {
	title: 'Markdown',
	component: MarkdownComponent,
} satisfies Meta<typeof MarkdownComponent>

export default meta
type Story = StoryObj<typeof meta>

const DEMO_PROSE = `
# Markdown content demonstration

The Markdown renderer supports all features of CommonMark, plus additional extensions targeted at making authoring
easier and more accessible.

## Standard markup

- **Bold**
- *Italics*
- \`Monospace\`
- ~~strikethrough~~

| Column 1 (Left aligned) | Column 2 (Middle aligned) | Column 3 (Right aligned) |
|:------------------------|:-------------------------:|-------------------------:|
| Row 1 Col 1             | Row 1 Col 2               | Row 1 Col 3              |
| Row 2 Col 1             | Row 2 Col 2               | Row 2 Col 3              |

\`\`\`md
- **Bold**
- *Italics*
- \`Monospace\`

| Column 1 (Left aligned) | Column 2 (Middle aligned) | Column 3 (Right aligned) |
|:------------------------|:-------------------------:|-------------------------:|
| Row 1 Col 1             | Row 1 Col 2               | Row 1 Col 3              |
| Row 2 Col 1             | Row 2 Col 2               | Row 2 Col 3              |
\`\`\`

## Additional markup

- Subscript: H~2~O
- Superscript: 29^th^
- ==Highlighted== text
- Footnotes[^foot-note]
- Abbreviations: The HTML specification is maintained by the W3C.

*[HTML]: Hyper Text Markup Language
*[W3C]: World Wide Web Consortium

[^foot-note]: I am a foot note. Hi!

\`\`\`md
- Subscript: H~2~O
- Superscript: 29^th^
- ==Highlighted== text
- Footnotes[^foot-note]
- Abbreviations: The HTML specification is maintained by the W3C.

*[HTML]: Hyper Text Markup Language
*[W3C]: World Wide Web Consortium

[^foot-note]: I am a foot note. Hi!
\`\`\`

## Callout

Obsidian-style callouts are supported.

> [!NOTE]
> Highlights information that users should take into account, even when skimming.

> [!TIP]
> Optional information to help a user be more successful.

> [!IMPORTANT]
> Crucial information necessary for users to succeed.

> [!WARNING]
> Critical content demanding immediate user attention due to potential risks.

> [!CAUTION]
> Negative potential consequences of an action.

> [!TIP] Extra tip
> It is possible to use a custom callout title.

\`\`\`md
> [!NOTE]
> Highlights information that users should take into account, even when skimming.

> [!TIP]
> Optional information to help a user be more successful.

> [!IMPORTANT]
> Crucial information necessary for users to succeed.

> [!WARNING]
> Critical content demanding immediate user attention due to potential risks.

> [!CAUTION]
> Negative potential consequences of an action.

> [!TIP] Extra tip
> It is possible to use a custom callout title.
\`\`\`

## Mathematics, chemistry, and other scientific stuff

There is support of $\\LaTeX$ expressions, rendered using MathML (which is fully compatible with screen readers).
It uses [$\\Temml$](https://temml.org/) under the hood which has extensive coverage even for complex expressions.

$\\Temml$ ships with an implementation of [mhchem](https://mhchem.github.io/MathJax-mhchem/) and
[physics](https://www.ctan.org/tex-archive/macros/latex/contrib/physics). $\\Temml$ also hooks into the browser's
Clipboard API: copying a rendered mathematical expression will copy the **$\\LaTeX$ markup**, not the rendered symbols.

Macros are **supported** and are usable throughout the document even across different math blocks.

While MathML is SOTA in terms of a11y, there are a handful
of [rendering quirks](https://temml.org/docs/en/administration#browser-issues) documented by $\\Temml$.

*[SOTA]: State of the art
*[a11y]: Accessibility

### Demo

Pythagorean theorem is $a^2 + b^2 = c^2$.

$$
\\def\\d{\\mathrm{d}}

\\oint_C \\vec{B}\\circ \\d\\vec{l} = \\mu_0 \\left( I_{\\text{enc}} + \\varepsilon_0 \\frac{\\d}{\\d t} \\int_S {\\vec{E} \\circ \\hat{n}}\\; \\d a \\right)
$$

Raisebox:

$$
\\raisebox{0pt}{\\Large%
\\textbf{Aaaa\\raisebox{-0.3ex}{a}%
\\raisebox{-0.7ex}{aa}\\raisebox{-1.2ex}{r}%
\\raisebox{-2.2ex}{g}\\raisebox{-4.5ex}{h}}}
$$

Complex chemistry:

$$
\\ce{Zn^2+ <=>[+ 2OH-][+ 2H+]
$\\underset{\\text{amphoteres Hydroxid}}
{\\ce{Zn(OH)2 v}}$ <=>[+ 2OH-][+ 2H+]
$\\underset{\\text{Hydroxozikat}}
{\\ce{[Zn(OH)4]^2-}}$}
$$

\`\`\`md
Pythagorean theorem is $a^2 + b^2 = c^2$.

$$
\\def\\d{\\mathrm{d}}

\\oint_C \\vec{B}\\circ \\d\\vec{l} = \\mu_0 \\left( I_{\\text{enc}} + \\varepsilon_0 \\frac{\\d}{\\d t} \\int_S {\\vec{E} \\circ \\hat{n}}\\; \\d a \\right)
$$

Raisebox:

$$
\\raisebox{0pt}{\\Large%
\\textbf{Aaaa\\raisebox{-0.3ex}{a}%
\\raisebox{-0.7ex}{aa}\\raisebox{-1.2ex}{r}%
\\raisebox{-2.2ex}{g}\\raisebox{-4.5ex}{h}}}
$$

Complex chemistry:

$$
\\ce{Zn^2+ <=>[+ 2OH-][+ 2H+]
$\\underset{\\text{amphoteres Hydroxid}}
{\\ce{Zn(OH)2 v}}$ <=>[+ 2OH-][+ 2H+]
$\\underset{\\text{Hydroxozikat}}
{\\ce{[Zn(OH)4]^2-}}$}
$$
\`\`\`

## Code highlighting

Syntax highlighting is powered by the [Shiki] library. Some extensions have been enabled for additional capabilities.

[Shiki]: https://shiki.style/

### Indent indicators

\`\`\`js
function meow(mrrp) {
	console.log('meow')
	if (mrrp) {
		console.log('mrrrp')
		console.log('meow')
	}
}
\`\`\`

### Diff

\`\`\`\`md
\`\`\`ts
console.log('hewwo') // [\\!code --]
console.log('hello') // [\\!code ++]
console.log('goodbye')
\`\`\`
\`\`\`\`

\`\`\`ts
console.log('hewwo') // [!code --]
console.log('hello') // [!code ++]
console.log('goodbye')
\`\`\`

### Highlight

\`\`\`\`md
\`\`\`ts
// [\\!code highlight:2]
console.log('highlighted')
console.log('highlighted')
console.log('not highlighted')
\`\`\`
\`\`\`\`

\`\`\`ts
// [!code highlight:2]
console.log('highlighted')
console.log('highlighted')
console.log('not highlighted')
\`\`\`

\`\`\`\`md
\`\`\`ts
console.log('Not highlighted')
console.log('Highlighted') // [\\!code highlight]
console.log('Not highlighted')
\`\`\`
\`\`\`\`

\`\`\`ts
console.log('Not highlighted')
console.log('Highlighted') // [!code highlight]
console.log('Not highlighted')
\`\`\`

### Word highlight

\`\`\`\`md
\`\`\`ts
// [\\!code word:Hello]
const message = 'Hello World'
console.log(message) // prints Hello World
\`\`\`
\`\`\`\`

\`\`\`ts
// [!code word:Hello]
const message = 'Hello World'
console.log(message) // prints Hello World
\`\`\`

\`\`\`\`md
\`\`\`ts
// [\\!code word:Hello:1]
const message = 'Hello World'
console.log(message) // prints Hello World
\`\`\`
\`\`\`\`

\`\`\`ts
// [!code word:Hello:1]
const message = 'Hello World'
console.log(message) // prints Hello World
\`\`\`

### Focus

\`\`\`\`md
\`\`\`ts
console.log('Not focused');
console.log('Focused') // [\\!code focus]
console.log('Not focused');
\`\`\`
\`\`\`\`

\`\`\`ts
console.log('Not focused');
console.log('Focused') // [!code focus]
console.log('Not focused');
\`\`\`

\`\`\`\`md
\`\`\`ts
// [\\!code focus:3]
console.log('Focused')
console.log('Focused')
console.log('Focused')
console.log('Not focused')
\`\`\`
\`\`\`\`

\`\`\`ts
// [!code focus:3]
console.log('Focused')
console.log('Focused')
console.log('Focused')
console.log('Not focused')
\`\`\`

### Error level

\`\`\`\`md
\`\`\`ts
console.log('No errors or warnings')
console.error('Error') // [\\!code error]
console.warn('Warning') // [\\!code warning]
console.log('Info') // [\\!code info]
\`\`\`
\`\`\`\`

\`\`\`ts
console.log('No errors or warnings')
console.error('Error') // [!code error]
console.warn('Warning') // [!code warning]
console.log('Info') // [!code info]
\`\`\`
`.trim()

export const Markdown: Story = {
	args: {
		markdown: DEMO_PROSE,
	},
}
