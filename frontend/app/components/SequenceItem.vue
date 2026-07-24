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

<script lang="ts" setup>
import { FetchError } from 'ofetch'

const { $api } = useNuxtApp()
const { uuid } = defineProps<{
	uuid: string
}>()
const lastPage = window.history.state.back as string

const sequenceData = ref<SciconumScenarioPhaseDto | null | undefined>(null)
const pending = ref(true)
const error = ref<undefined | object>(undefined)

const phase = ref<string | undefined>(undefined)
const state = ref<string | undefined>(undefined)
const duration = ref('')

const skeleton = computed(() => pending.value)

const name = computed(
	() => sequenceData.value?.sequence.name ?? 'This sequence does not exists',
)
const currentRound = computed(() => sequenceData.value?.currentRound ?? 0)
const question = computed(
	() =>
		sequenceData.value?.sequence.sciconumQuestions[currentRound.value]
			?.statement.content ?? '',
)

async function fetchSequenceState() {
	try {
		const response = await $api(
			'/v1/player/org.elaastix.engine.getSciconumSequenceSession',
			{
				method: 'POST',
				query: {
					scenarioSessionId: uuid,
				},
			},
		)
		sequenceData.value = response
		phase.value = response.phase
	} catch (e) {
		if (e instanceof FetchError) {
			error.value = {
				statusCode: e.statusCode,
				statusMessage: e.statusMessage,
				message: e.message,
			}
		} else {
			console.error('Failed to fetch sequence state:', e)
		}
	} finally {
		pending.value = false
	}
}

onMounted(async () => {
	fetchSequenceState()
	useWebSocket({
		onOpen: () => {
			console.log('The websocket opened')
		},
		onMessage: (dataReceived) => {
			console.log(
				`data received: ${JSON.stringify(dataReceived, null, 2)}`,
			)
			phase.value = dataReceived.sciconumPhase
			state.value = dataReceived.state
			duration.value = dataReceived.duration ?? ''
		},
		onClose: () => {
			console.log('The websocket closed')
		},
		onError: () => {
			console.log('The websocket encountered an error')
		},
	})
})

function startSequence() {
	$api('/v1/player/org.elaastix.engine.startSciconumScenarioSession', {
		method: 'POST',
		query: {
			scenarioSessionId: uuid as string,
		},
	})
}
</script>

<template>
	<UCard class="w-full h-full">
		<template #header>
			<USkeleton
				v-if="skeleton"
				class="w-full h-1/4"
			/>
			<UError
				v-else-if="error"
				:redirect="lastPage"
				class="w-full h-full"
				icon="i-lucide-circle-x"
				:error="error"
			/>
			<UPageCard
				v-else
				class="w-full h-full"
			>
				<div class="flex justify-between">
					<div class="flex flex-col gap-2">
						<div class="text-xl">
							{{ name }}
						</div>
						<div>{{ question }}</div>
					</div>
					<div class="flex flex-col items-center gap-1">
						<div>{{ phase }}</div>
						<UButton
							:icon="
								state === 'PENDING' || state === undefined
									? 'i-lucide-circle-play'
									: ''
							"
							:disabled="!(state === 'PENDING' || state === undefined)"
							size="lg"
							@click="startSequence"
						>
							{{ state }}
						</UButton>
					</div>
				</div>
			</UPageCard>
		</template>
	</UCard>
</template>
