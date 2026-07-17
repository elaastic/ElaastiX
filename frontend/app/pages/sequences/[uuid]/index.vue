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
const uuid = useRoute().params.uuid

const { data, status: requestStatus } = useApi(`/v1/sequences/{id}`, {
	path: {
		id: uuid as string,
	},
})

const skeleton = computed(
	() => requestStatus.value !== 'success' && requestStatus.value !== 'error',
)
const name = computed(() => data.value?.name ?? 'This sequence doesnt exists')

useWebSocket({
	onOpen: () => {
		console.log('The websocket opened')
	},
	onMessage: () => {
		console.log('The websocket received a message')
	},
	onClose: () => {
		console.log('The websocket closed')
	},
	onError: () => {
		console.log('The websocket encountered an error')
	},
})
</script>

<template>
	<UCard class="w-full h-full p-4">
		<template #header>
			<USkeleton
				v-if="skeleton"
				class="w-full h-full"
			/>
			<div class="w-full h-full">
				{{ name }}
			</div>
		</template>
	</UCard>
</template>
