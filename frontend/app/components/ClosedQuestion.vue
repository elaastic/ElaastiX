<!--
  - Elaastic / ElaastiX - formative assessment system
  - Copyright (C) 2019  Université de Toulouse and Université Toulouse Capitole.
  - SPDX-License-Identifier: AGPL-3.0-or-later
  -
  - This program is free software: you can redistribute it and/or modify
  - it under the terms of the GNU Affero General Public License as published by
  - the Free Software Foundation, either version 3 of the License, or
  - (at your option) any later version.
  -
  - This program is distributed in the hope that it will be useful,
  - but WITHOUT ANY WARRANTY; without even the implied warranty of
  - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  - GNU Affero General Public License for more details.
  -
  - You should have received a copy of the GNU Affero General Public License
  - along with this program.  If not, see <http://www.gnu.org/licenses/>.
-->

<script lang="ts">
interface Props {
	/**
	 * The question statement.
	 */
	question: ClosedQuestionStatementDto
	/**
	 * Whether to request a self explanation or not. If false, then the `selfExplanation` field of the response will
	 * always be `null`.
	 */
	requestSelfExplanation?: boolean
	/**
	 * The number of options on the confidence degree Likert scale, of `false` to disable it. If false, then the
	 * `confidenceDegree` field of the response will always be `null`. Otherwise, the confidence degree will always
	 * be defined, with a value of `0` if the respondant did not provide it.
	 */
	confidenceDegreeOptions?: false | 3 | 4 | 5 | 6 | 7

	/**
	 * A flag signaling the response is being sent to the server.
	 */
	submitting?: boolean
	/**
	 * An error message if the response failed to be sent.
	 */
	error?: string
}

type Output = {
	answer: number | number[] | null
	selfExplanation: string | null
	confidenceDegree: number | null
}

interface Emits {
	/** The response submitted by the learner. */
	submit: [Output]
}
</script>

<script setup lang="ts">
import * as v from 'valibot'
import { LikertScaleType } from '~/components/form/Likert.vue'
import { URadioGroup, UCheckboxGroup } from '#components'
import ContentRenderer from './ContentRenderer.vue'
import SubmitButton from './SubmitButton.vue'

const {
	question,
	requestSelfExplanation = true,
	confidenceDegreeOptions = 4,
} = defineProps<Props>()
const emit = defineEmits<Emits>()

const schema = computed(() => {
	const answerItem = v.pipe(
		v.number(),
		v.integer(),
		v.minValue(0),
		v.maxValue(question.choices.length - 1),
	)

	return v.object({
		answer: v.nullable(
			question.multiple ? v.array(answerItem) : answerItem,
		),
		selfExplanation: requestSelfExplanation
			? v.nullable(v.pipe(v.string(), v.maxBytes(4000)))
			: v.null(),
		confidenceDegree: confidenceDegreeOptions
			? v.nullable(
					v.pipe(
						v.number(),
						v.minValue(0),
						v.maxValue(confidenceDegreeOptions - 1),
					),
					0,
				) // If null then assume 0
			: v.null(),
	})
})

const response = reactive<Output>({
	answer: question.multiple ? [] : null,
	selfExplanation: null,
	confidenceDegree: null,
})

const options = computed(() =>
	question.choices.map((c, i) => ({ label: c, value: i })),
)
const state = computed(() => ({
	answer:
		(Array.isArray(response.answer) && response.answer.length > 0)
		|| (!Array.isArray(response.answer) && response.answer !== null),
	selfExplanation:
		!requestSelfExplanation || response.selfExplanation !== null,
	confidenceDegree:
		!confidenceDegreeOptions || response.confidenceDegree !== null,
}))

const confirmRequest = ref(false)
watch(response, () => (confirmRequest.value = false))
function handleSubmit() {
	if (
		!confirmRequest.value
		&& (!state.value.answer
			|| !state.value.selfExplanation
			|| !state.value.confidenceDegree)
	) {
		confirmRequest.value = true
	} else {
		emit('submit', response)
	}
}
</script>

<template>
	<ContentRenderer :content="question.statement" />
	<hr class="my-4 bg-gray-300">
	<UForm
		class="flex flex-col gap-4"
		:schema="schema"
		:state="response"
	>
		<UFormField
			:label="$t('activity.responses.answer-field-label')"
			name="answer"
		>
			<component
				:is="question.multiple ? UCheckboxGroup : URadioGroup"
				v-model="response.answer"
				:items="options"
				orientation="horizontal"
				variant="card"
				:ui="{
					fieldset:
						'grid grid-cols-[repeat(auto-fit,minmax(200px,1fr))]',
				}"
			>
				<template #label="{ item }">
					<ContentRenderer :content="item.label" />
				</template>
			</component>
		</UFormField>

		<UFormField
			v-if="requestSelfExplanation"
			:label="$t('activity.responses.self-explanation-field-label')"
			name="selfExplanation"
		>
			<UTextarea
				v-model="response.selfExplanation!"
				class="w-full"
			/>
		</UFormField>
		<FormHidden
			v-else
			v-model="response.selfExplanation"
			name="selfExplanation"
			:const="null"
		/>

		<UFormField
			v-if="confidenceDegreeOptions"
			:label="$t('activity.responses.confidence-degree-field-label')"
			name="confidenceDegree"
		>
			<FormLikert
				v-model="response.confidenceDegree"
				:type="LikertScaleType.CONFIDENCE"
				:points="confidenceDegreeOptions"
			/>
		</UFormField>
		<FormHidden
			v-else
			v-model="response.confidenceDegree"
			name="confidenceDegree"
			:const="null"
		/>

		<SubmitButton
			:confirm-request="confirmRequest"
			:submitting="submitting"
			:value-presence="state.answer"
			:error="error"
			@on-submit="handleSubmit"
		/>
	</UForm>
</template>
