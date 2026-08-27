<script setup lang="ts">
import * as v from 'valibot'
import type { components } from '#open-fetch-schemas/api'
import Likert, { LikertScaleType } from './form/Likert.vue'
import { ChoiceHasText } from '~/lib/utils'
import SubmitButton from './SubmitButton.vue'

interface Props {
	isLoading: boolean
	error: Error | undefined
	question: components['schemas']['ClosedQuestionStatementDto'] | undefined
	peerChoice: number
	peerExplanation:
		| components['schemas']['MarkdownText']
		| components['schemas']['PlainText']
		| undefined
	submitting?: boolean
	errorSubmitting?: string
}

const schema = v.object({
	judgement: v.optional(v.number()),
})

type Judgement = v.InferOutput<typeof schema>

interface Emits {
	submit: [Judgement]
}

const state = reactive({
	judgement: undefined,
})

defineProps<Props>()
const emit = defineEmits<Emits>()

const confirmRequest = ref(false)
watch(state, () => (confirmRequest.value = false))

function onSubmit() {
	if (!confirmRequest.value && !state.judgement) {
		confirmRequest.value = true
	} else {
		emit('submit', state)
	}
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

		<UError
			v-if="question === undefined"
			:error="{ message: 'oops', name: 'unexpected' }"
		/>

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
			<p
				v-if="ChoiceHasText(question!.choices[peerChoice])"
				class="text-xl"
			>
				{{ $t("feedback.why") }}
				<span class="text-secondary">
					<ContentRenderer
						:content="question!.choices[peerChoice]!"
					/>
				</span>
				?
			</p>
			<div
				v-if="ChoiceHasText(peerExplanation)"
				class="p-4 border border-neutral rounded-lg inset-shadow-sm"
			>
				<ContentRenderer :content="peerExplanation!" />
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
						v-model="state.judgement"
						:type="LikertScaleType.AGREEMENT"
						:points="5"
					/>
				</UFormField>
				<SubmitButton
					:confirm-request="confirmRequest"
					:error="errorSubmitting"
					:submitting="submitting"
					:value-presence="state.judgement!!"
					@on-submit="onSubmit"
				/>
			</UForm>
		</div>
	</DataPage>
</template>
