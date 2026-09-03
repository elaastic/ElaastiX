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
import type { SequenceExecution } from '~/types/sequence'

interface Props {
	sequenceExecution: SequenceExecution
}

interface Emits {
	(e: 'resumeSequence' | 'startSequence' | 'pauseSequence'): void
}

const { sequenceExecution } = defineProps<Props>()
const emits = defineEmits<Emits>()
</script>

<template>
	<UCard>
		<template #header>
			<SequenceHeader :sequence-execution="sequenceExecution" />

			<div class="flex justify-end items-center gap-1">
				<!-- TODO: We need to update the server so that the state is never undefined -->
				<UButton
					v-if="sequenceExecution.state === 'PENDING' || sequenceExecution.state === undefined"
					icon="i-lucide-play"
					@click="emits('startSequence')"
				>
					{{ sequenceExecution.state }}
				</UButton>
				<UButton
					v-if="sequenceExecution.state === 'RUNNING'"
					icon="i-lucide-pause"
					@click="emits('pauseSequence')"
				>
					{{ sequenceExecution.state }}
				</UButton>
				<UButton
					v-if="sequenceExecution.state === 'PAUSED'"
					icon="i-lucide-play"
					@click="emits('resumeSequence')"
				>
					{{ sequenceExecution.state }}
				</UButton>
				<UButton v-if="sequenceExecution.state === 'END'">
					{{ sequenceExecution.state }}
				</UButton>
			</div>
		</template>

		<div>{{ sequenceExecution.question?.statement.content ?? "" }}</div>
	</UCard>
</template>
