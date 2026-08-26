<script setup lang="ts">
import type { components } from '#open-fetch-schemas/api'

interface Props {
	isLoading: boolean
	error: Error | undefined
	question: components['schemas']['ClosedQuestionStatementDto'] | undefined
	learnerChoice: number | undefined
	learnerExplanation:
		| components['schemas']['MarkdownText']
		| components['schemas']['PlainText']
		| undefined
	correctionChoice: number
	explanatoryFeedback:
		| components['schemas']['MarkdownText']
		| components['schemas']['PlainText']
}

const { learnerChoice, correctionChoice, question } = defineProps<Props>()

function getIcon(index: number) {
	if (index !== learnerChoice) return ''
	return index === correctionChoice ? 'i-lucide-check' : 'i-lucide-x'
}

const feedbackChoices = computed(() =>
	question?.choices.map((choice, index) => ({
		value: choice,
		color: (index === correctionChoice ? 'primary' : 'error') as
			'primary' | 'error',
		icon: getIcon(index),
	})),
)

function hasText(
	content:
		| components['schemas']['MarkdownText']
		| components['schemas']['PlainText']
		| undefined,
) {
	if (content === undefined) return false
	return content.notBlank && content.notEmpty
}
</script>

<template>
	<DataPage
		:is-loading="isLoading"
		:error="error"
		:is-empty="false"
	>
		<template #loading>
			<USkeleton
				v-for="i in Array.from({ length: 4 }, (_, i) => i + 1)"
				:key="i"
				class="w-full h-1/8"
			/>
		</template>

		<div class="flex flex-col gap-4 wrap-anywhere">
			<ContentRenderer :content="question!.statement" />
			<div
				class="gap-4 grid grid-cols-[repeat(auto-fit,minmax(200px,1fr))]"
			>
				<UBadge
					v-for="choice in feedbackChoices"
					:key="choice.value.content"
					variant="outline"
					size="lg"
					:color="choice.color"
					:icon="choice.icon"
				>
					<ContentRenderer :content="choice.value" />
				</UBadge>
			</div>
			<p class="text-xl">
				{{ $t("feedback.yourExplanation") }} :
			</p>
			<div
				v-if="hasText(learnerExplanation)"
				class="p-4 border border-neutral rounded-lg inset-shadow-sm"
			>
				<ContentRenderer :content="learnerExplanation!" />
			</div>
			<p v-else>
				{{ $t("feedback.noAnswer") }}
			</p>
			<p class="text-xl">
				{{ $t("feedback.why") }}
				<span class="text-primary">
					<ContentRenderer
						:content="question!.choices[correctionChoice]!"
					/>
				</span>
				?
			</p>
			<div class="p-4 border border-primary rounded-lg inset-shadow-sm">
				<ContentRenderer :content="explanatoryFeedback" />
			</div>
		</div>
	</DataPage>
</template>
