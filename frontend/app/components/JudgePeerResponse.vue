<script setup lang="ts">
import * as v from 'valibot'
import type { components } from '#open-fetch-schemas/api'
import Likert, { LikertScaleType } from './form/Likert.vue'

interface Props {
	isLoading: boolean
	error: Error | undefined
	question: components['schemas']['ClosedQuestionStatementDto']
	peerChoice: number
	peerExplanation:
		| components['schemas']['MarkdownText']
		| components['schemas']['PlainText']
}

const schema = v.object({
	confidence: v.number(),
})

type Output = v.InferOutput<typeof schema>

interface Emits {
	submit: [Output]
}

const state = reactive({
	confidence: 2,
})

defineProps<Props>()
const emit = defineEmits<Emits>()

function onSubmit() {
	emit('submit', state)
}
</script>

<template>
	<DataPage
		:is-loading="isLoading"
		:error="error"
		:is-empty="false"
		is-empty-message=""
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
					v-for="(choice, index) in question?.choices"
					:key="choice.content"
					variant="outline"
					size="lg"
					:color="index === peerChoice ? 'secondary' : 'neutral'"
					:icon="index === peerChoice ? 'i-lucide-check' : ''"
				>
					<ContentRenderer :content="choice" />
				</UBadge>
			</div>
			<p class="text-xl">
				{{ $t("feedback.why") }}
				<span class="text-secondary">
					<ContentRenderer
						:content="question!.choices[peerChoice]!"
					/>
				</span>
				?
			</p>
			<div class="p-4 border border-secondary rounded-lg inset-shadow-sm">
				<ContentRenderer :content="peerExplanation" />
			</div>
			<UForm
				:schema="schema"
				:state="state"
				class="flex flex-col gap-4"
				@submit="onSubmit"
			>
				<UFormField
					class="text-xl"
					:label="$t('activity.peer.agreement-label')"
					name="confidence"
				>
					<Likert
						v-model="state.confidence"
						:type="LikertScaleType.AGREEMENT"
						:points="5"
					/>
				</UFormField>
				<div class="flex justify-end items-right">
					<UButton
						variant="link"
						size="lg"
						:ui="{ trailingIcon: 'size-3' }"
						class="inline-flex items-center p-0 cursor-pointer gap-1"
						trailing-icon="i-lucide-arrow-right"
						@click="onSubmit"
					>
						{{ $t("activity.responses.submit") }}
					</UButton>
				</div>
			</UForm>
		</div>
	</DataPage>
</template>
