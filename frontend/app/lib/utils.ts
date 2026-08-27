import type { components } from '#open-fetch-schemas/api'

export function ChoiceHasText(
	content:
		| components['schemas']['MarkdownText']
		| components['schemas']['PlainText']
		| undefined,
) {
	if (content === undefined) return false
	return content.notBlank && content.notEmpty
}
