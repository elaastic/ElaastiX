<script setup lang="ts">
import type { components } from '#open-fetch-schemas/api'

interface Props {
	isLoading: boolean
	error: Error | undefined
	question: components['schemas']['ClosedQuestionStatementDto'] | undefined
	learnerChoice: number | undefined
	learnerExplanation: components['schemas']['FormattedContent']
	correctionChoice: number
	explanatoryFeedback: components['schemas']['FormattedContent']
}

const { learnerChoice, correctionChoice } = defineProps<Props>()

function getIcon(index: number) {
	if (index !== learnerChoice) return ''
	return index === correctionChoice ? 'i-lucide-check' : 'i-lucide-x'
}
</script>

<template>
	<DataPage
		:is-loading="isLoading"
		:error="error"
		:is-empty="false"
		:is-empty-message="''"
	>
		<template #loading>
			<USkeleton
				v-for="i in Array.from({ length: 4 }, (_, i) => i + 1)"
				:key="i"
				class="w-full h-1/8"
			/>
		</template>

		<div class="flex flex-col gap-4">
			<div>{{ question?.statement.content ?? "" }}</div>
			<div
				class="gap-4 grid grid-cols-[repeat(auto-fit,minmax(200px,1fr))]"
			>
				<UBadge
					v-for="(choice, index) in question?.choices"
					:key="choice.content"
					variant="outline"
					size="lg"
					:color="index === correctionChoice ? 'primary' : 'error'"
					:icon="getIcon(index)"
				>
					<span class="wrap-anywhere">{{ choice.content }}</span>
				</UBadge>
			</div>
			<p>{{ $t("feedback.yourExplanation") }} :</p>
			<UTextarea
				:model-value="learnerExplanation"
				:readonly="true"
				:disabled="true"
				autoresize
			/>
			<p>
				{{ $t("feedback.why") }}
				<span class="text-primary">
					{{ question?.choices[correctionChoice]?.content }}
				</span>
				?
			</p>
			<UTextarea
				:model-value="explanatoryFeedback"
				color="primary"
				highlight
				:readonly="true"
				autoresize
			/>
		</div>
	</DataPage>
</template>
