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
const { uuid } = defineProps<{
	uuid: string
}>()

const { data } = useSequence(uuid)

const currentRound = computed(() => data.value?.currentRound ?? 0)
const question = computed(
	() => data.value?.sequence.sciconumQuestions[currentRound.value],
)
const closedQuestion = computed(() =>
	question.value?.$type === 'ClosedQuestion' ? question.value : null,
)
</script>

<template>
	<ClosedQuestion
		v-if="closedQuestion"
		:question="closedQuestion"
	/>
	<!-- TODO: support open questions -->
</template>
