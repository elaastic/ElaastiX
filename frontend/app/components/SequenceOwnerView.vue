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
}

interface Emits {
	(e: 'resumeSequence' | 'startSequence' | 'pauseSequence'): void
}

const { data, state } = defineProps<Props>()
const emits = defineEmits<Emits>()

const name = computed(
	() => data?.sequence.name ?? 'This sequence does not exists',
)
const currentRound = computed(() => data?.currentRound ?? 0)
const question = computed(
	() =>
		data?.sequence.sciconumQuestions[currentRound.value]?.statement
			.content ?? '',
)
const phase = computed(() => data?.phase)
</script>

<template>
	<UPageCard
		class="w-full h-min"
		:highlight="lessThan10secForCurrentSeq"
		highlight-color="error"
	>
		<div class="flex justify-between">
			<div class="flex flex-col gap-2">
				<div class="text-xl">
					{{ name }}
				</div>
				<div
					v-if="lastingTime !== ''"
					class="text-sm text-muted -mt-2"
				>
					{{ $t("sequence.remaining") }}
					{{ lastingTime }}
				</div>
				<div>{{ question }}</div>
			</div>
			<div class="flex flex-col items-center gap-1">
				<div>{{ phase }}</div>

				<!-- TODO: We need to update the server so that the state is never undefined -->
				<UButton
					v-if="state === 'PENDING' || state === undefined"
					icon="i-lucide-play"
					@click="emits('startSequence')"
				>
					{{ state }}
				</UButton>
				<UButton
					v-if="state === 'RUNNING'"
					icon="i-lucide-pause"
					@click="emits('pauseSequence')"
				>
					{{ state }}
				</UButton>
				<UButton
					v-if="state === 'PAUSED'"
					icon="i-lucide-play"
					@click="emits('resumeSequence')"
				>
					{{ state }}
				</UButton>
				<UButton v-if="state === 'END'">
					{{ state }}
				</UButton>
			</div>
		</div>
	</UPageCard>
</template>
