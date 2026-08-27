import type { components } from '#open-fetch-schemas/api'

function choiceFactory(
	content: string,
	$type: 'PlainText' | 'MarkdownInline',
): components['schemas']['MarkdownText'] | components['schemas']['PlainText'] {
	return {
		$type,
		content,
		notEmpty: true,
		notBlank: true,
	}
}

function statementFactory(
	content: string,
	$type: 'PlainText' | 'Markdown' | 'MarkdownInline',
):
	| components['schemas']['MarkdownContent']
	| components['schemas']['MarkdownText']
	| components['schemas']['PlainText'] {
	return {
		$type,
		content,
		notEmpty: true,
		notBlank: true,
	}
}

function plainTextFactory(content: string): components['schemas']['PlainText'] {
	return {
		content,
		notBlank: true,
		notEmpty: true,
		$type: 'PlainText',
	}
}

function markdownInlineFactory(
	content: string,
): components['schemas']['MarkdownText'] {
	return {
		content,
		notBlank: true,
		notEmpty: true,
		$type: 'MarkdownInline',
	}
}

export const lorem1000
	= 'Lorem ipsum dolor sit amet consectetur adipiscing elit quisque faucibus ex sapien vitae pellentesque sem placerat in id cursus mi pretium tellus duis convallis tempus leo eu aenean sed diam urna tempor pulvinar vivamus fringilla lacus nec metus bibendum egestas iaculis massa nisl malesuada lacinia integer nunc posuere ut hendrerit semper vel class aptent taciti sociosqu ad litora torquent per conubia nostra inceptos himenaeos orci various natoque penatibus et magnis dis parturient montes nascetur ridiculous mus donec rhoncus eros lobortis nulla molestie mattis scelerisque maximus eget fermentum odio phasellus non purus est efficitur laoreet mauris pharetra vestibulum fusce dictum risus.'

export const error = {
	name: 'this is an error name',
	message: 'this is an error message',
}

export const singleChoiceQuestion = {
	id: '0000000000000000000000000',
	$type: 'ClosedQuestion',
	statement: statementFactory(
		'What is the best multipurpose operating system?',
		'Markdown',
	),
	multiple: false,
	choices: [
		choiceFactory('Linux', 'PlainText'),
		choiceFactory('Windows', 'PlainText'),
		choiceFactory('Darwin (macOS)', 'PlainText'),
		choiceFactory('OpenBSD', 'PlainText'),
	],
} satisfies components['schemas']['ClosedQuestionStatementDto']

export const multipleChoiceQuestionMarkdown = {
	id: '0000000000000000000000000',
	$type: 'ClosedQuestion',
	statement: statementFactory(
		'**TRUE OR FALSE**. Software written in Haskell is guaranteed to have no side-effects.',
		'Markdown',
	),
	multiple: true,
	choices: [
		choiceFactory(
			'**TRUE**. Haskell is a functional programming language, and therefore pure',
			'MarkdownInline',
		),
		choiceFactory(
			'**FALSE**. It is impossible to write side-effect-free code',
			'MarkdownInline',
		),
		choiceFactory(
			'**TRUE**. No one run Haskell software, and therefore no side-effect ever occurs',
			'MarkdownInline',
		),
		choiceFactory(
			'**FALSE**. Haskell wraps "impure" logic using monadic structures',
			'MarkdownInline',
		),
	],
} satisfies components['schemas']['ClosedQuestionStatementDto']

export const learnerExplanation = plainTextFactory('Is it even a question')

export const explanation = plainTextFactory(
	'Because it is. signed the professor.',
)

export const learnerExplanationMarkdown
	= markdownInlineFactory('*Its obvious.*')

export const explanationMarkdown = markdownInlineFactory(
	'See chapter 3 : `man kernel`.',
)

export const questionMarkdown = {
	id: '0000000000000000000000000',
	$type: 'ClosedQuestion',
	statement: statementFactory(
		`### Choix d'un système d'exploitation

Considérez les critères suivants :
- **Stabilité** du noyau (*kernel*)
- Support des environments de contenders (\`Docker\`, \`Podman\`)
- Flexibilité et gestion des droits (POSIX)

Quel est le **meilleur** système d'exploitation polyvalent ?`,
		'Markdown',
	),
	multiple: false,
	choices: [
		choiceFactory('**Linux** (`GNU/Linux`)', 'MarkdownInline'),
		choiceFactory('**Windows** (`NT Kernel`)', 'MarkdownInline'),
		choiceFactory('**Darwin** (*macOS*)', 'MarkdownInline'),
		choiceFactory('**OpenBSD** (*Focus sécurité*)', 'MarkdownInline'),
	],
} satisfies components['schemas']['ClosedQuestionStatementDto']
