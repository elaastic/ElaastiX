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

<script setup lang="ts">
import type { components } from '#open-fetch-schemas/api'
import { ClosedQuestion } from '#components'

interface Props {
	question:
		| components['schemas']['ClosedQuestionStatementDto']
		| components['schemas']['OpenQuestionStatementDto']
}

const { question } = defineProps<Props>()

// TODO Provide support for open questions
const questionComponents = {
	ClosedQuestion,
} as const

const isSupportedQuestionType = computed(
	() => question.$type in questionComponents,
)
</script>

<template>
	<UAlert
		v-if="!isSupportedQuestionType"
		class="mb-4"
	>
		Question type not supported
	</UAlert>
	<component
		:is="questionComponents[question.$type]"
		v-else
		:question="question"
	/>
</template>
