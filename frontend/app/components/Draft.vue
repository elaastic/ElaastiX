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
	question: ClosedQuestionStatementDto
}
</script>

<script setup lang="ts">
import * as v from 'valibot'
import { LikertScaleType } from '~/components/form/Likert.vue'
import { UCheckboxGroup, URadioGroup } from '#components'

const { question = {
	multiple: true,
} } = defineProps<Props>()

const schema = v.object({
	answer: v.pipe(v.string(), v.email('Invalid email')),
	justification: v.pipe(v.string(), v.minLength(8, 'Must be at least 8 characters')),
	confidenceDegree: v.pipe(v.string(), v.minLength(8, 'Must be at least 8 characters')),
})

const rep = ref()
const skipping = ref(false)
</script>

<template>
	<p>
		What is the best multipurpose operating system?
	</p>
	<hr class="my-4 bg-gray-300"/>
	<UForm class="flex flex-col gap-4">
		<UFormField label="Your answer" name="answer">
			<component
				v-model="rep"
				:is="question.multiple ? UCheckboxGroup : URadioGroup"
				:items="['Linux', 'Windows', 'macOS', 'OpenBSD']"
				orientation="horizontal"
				variant="card"
				:ui="{
					fieldset: 'grid grid-cols-[repeat(auto-fit,minmax(200px,1fr))]'
				}"
			/>
		</UFormField>
		<UFormField label="Please provide an explanation for your answer" name="answer">
			<UTextarea class="w-full"/>
		</UFormField>
		<UFormField label="How confident are you in your answer?" name="answer">
			<FormLikert :type="LikertScaleType.CONFIDENCE" :points="4" />
		</UFormField>
		<div class="flex justify-end items-right">
			<div class="flex flex-col items-end gap-1">
				<div class="flex justify-end" v-if="!rep">
					<UButton class="cursor-pointer disabled:cursor-default" :disabled="skipping" @click="skipping = true">Skip</UButton>
				</div>
				<div class="flex justify-end" v-else>
					<UButton class="cursor-pointer">Submit</UButton>
				</div>
				<div v-if="skipping" class="flex items-center gap-1 text-xs">
					<span>Once skipped you will not be able to answer.</span>
					<UButton :ui="{ trailingIcon: 'size-3' }" class="p-0 text-xs cursor-pointer gap-1" variant="link" trailing-icon="i-lucide-arrow-right">
						Skip question
					</UButton>
				</div>
			</div>
		</div>
	</UForm>
</template>
