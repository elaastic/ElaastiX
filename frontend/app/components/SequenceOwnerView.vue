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
import type { State } from '~/lib/ScenarioTransitionMessage'

interface Props {
	data: components['schemas']['SciconumScenarioPhaseDto']
	state: State | undefined
	lastingTime: string
	lessThan10secForCurrentSeq: boolean
	name: string
	question:
		| components['schemas']['ClosedQuestionStatementDto']
		| components['schemas']['OpenQuestionStatementDto']
		| undefined
	phase: string | undefined
}

interface Emits {
	(e: 'resumeSequence' | 'startSequence' | 'pauseSequence'): void
}

defineProps<Props>()
const emits = defineEmits<Emits>()
</script>

<template>
	<div class="flex justify-between items-center gap-1">
		<div>{{ question?.statement.content ?? "" }}</div>

		<div class="w-1/6">
			<!-- TODO: We need to update the server so that the state is never undefined -->
			<UButton
				v-if="state === 'PENDING' || state === undefined"
				icon="i-lucide-play"
				class="w-full justify-center"
				@click="emits('startSequence')"
			>
				{{ state }}
			</UButton>
			<UButton
				v-if="state === 'RUNNING'"
				icon="i-lucide-pause"
				class="w-full justify-center"
				@click="emits('pauseSequence')"
			>
				{{ state }}
			</UButton>
			<UButton
				v-if="state === 'PAUSED'"
				icon="i-lucide-play"
				class="w-full justify-center"
				@click="emits('resumeSequence')"
			>
				{{ state }}
			</UButton>
			<UButton
				v-if="state === 'END'"
				class="w-full justify-center"
			>
				{{ state }}
			</UButton>
		</div>
	</div>
</template>
