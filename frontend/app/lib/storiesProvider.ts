import type { components } from '#open-fetch-schemas/api'

export const lorem1000
	= 'Lorem ipsum dolor sit amet consectetur adipiscing elit quisque faucibus ex sapien vitae pellentesque sem placerat in id cursus mi pretium tellus duis convallis tempus leo eu aenean sed diam urna tempor pulvinar vivamus fringilla lacus nec metus bibendum egestas iaculis massa nisl malesuada lacinia integer nunc posuere ut hendrerit semper vel class aptent taciti sociosqu ad litora torquent per conubia nostra inceptos himenaeos orci various natoque penatibus et magnis dis parturient montes nascetur ridiculous mus donec rhoncus eros lobortis nulla molestie mattis scelerisque maximus eget fermentum odio phasellus non purus est efficitur laoreet mauris pharetra vestibulum fusce dictum risus.'

export const error = {
	name: 'this is an error name',
	message: 'this is an error message',
}

export const singleChoiceQuestion = {
	id: '0000000000000000000000000',
	$type: 'ClosedQuestion',
	statement: {
		$type: 'Markdown',
		content: 'What is the best multipurpose operating system?',
	},
	multiple: false,
	choices: [
		{ $type: 'PlainText', content: 'Linux' },
		{ $type: 'PlainText', content: 'Windows' },
		{ $type: 'PlainText', content: 'Darwin (macOS)' },
		{ $type: 'PlainText', content: 'OpenBSD' },
	],
} satisfies components['schemas']['ClosedQuestionStatementDto']

export const multipleChoiceQuestionMarkdown = {
	id: '0000000000000000000000000',
	$type: 'ClosedQuestion',
	statement: {
		$type: 'Markdown',
		content:
			'**TRUE OR FALSE**. Software written in Haskell is guaranteed to have no side-effects.',
	},
	multiple: true,
	choices: [
		{
			$type: 'MarkdownInline',
			content:
				'**TRUE**. Haskell is a functional programming language, and therefore pure',
		},
		{
			$type: 'MarkdownInline',
			content:
				'**FALSE**. It is impossible to write side-effect-free code',
		},
		{
			$type: 'MarkdownInline',
			content:
				'**TRUE**. No one run Haskell software, and therefore no side-effect ever occurs',
		},
		{
			$type: 'MarkdownInline',
			content:
				'**FALSE**. Haskell wraps "impure" logic using monadic structures',
		},
	],
} satisfies components['schemas']['ClosedQuestionStatementDto']

export const learnerExplanation = {
	content: 'Is it even a question',
	notBlank: true,
	notEmpty: true,
	$type: 'PlainText',
} satisfies components['schemas']['PlainText']

export const explanation = {
	content: 'Because it is. signed the professor.',
	notBlank: true,
	notEmpty: true,
	$type: 'PlainText',
} satisfies components['schemas']['PlainText']

export const learnerExplanationMarkdown = {
	content: '*Its obvious.*',
	notBlank: true,
	notEmpty: true,
	$type: 'MarkdownInline',
} satisfies components['schemas']['MarkdownText']

export const explanationMarkdown = {
	content: 'See chapter 3 : `man kernel`.',
	notBlank: true,
	notEmpty: true,
	$type: 'MarkdownInline',
} satisfies components['schemas']['MarkdownText']

export const questionMarkdown = {
	id: '0000000000000000000000000',
	$type: 'ClosedQuestion',
	statement: {
		$type: 'Markdown',
		content: `### Choix d'un système d'exploitation

Considérez les critères suivants :
- **Stabilité** du noyau (*kernel*)
- Support des environments de contenders (\`Docker\`, \`Podman\`)
- Flexibilité et gestion des droits (POSIX)

Quel est le **meilleur** système d'exploitation polyvalent ?`,
	},
	multiple: false,
	choices: [
		{ $type: 'MarkdownInline', content: '**Linux** (`GNU/Linux`)' },
		{ $type: 'MarkdownInline', content: '**Windows** (`NT Kernel`)' },
		{ $type: 'MarkdownInline', content: '**Darwin** (*macOS*)' },
		{ $type: 'MarkdownInline', content: '**OpenBSD** (*Focus sécurité*)' },
	],
} satisfies components['schemas']['ClosedQuestionStatementDto']
