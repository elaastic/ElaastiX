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
/**
 * Generic page component that can be used to display data fetched asynchronously.
 * It handles:
 * - the shared page style
 * - the loading state
 * - the error state
 * - the empty state
 */
interface Props {
	isLoading: boolean
	error: Error | undefined
	isEmpty: boolean
	isEmptyMessage?: string
}

const { isLoading, error, isEmpty, isEmptyMessage } = defineProps<Props>()
</script>

<template>
	<div class="w-full h-full p-4">
		<slot
			v-if="isLoading"
			name="loading"
		/>
		<slot
			v-else-if="error"
			name="error"
		>
			<UError
				class="w-full h-full"
				icon="i-lucide-circle-x"
				:error="error"
			/>
		</slot>

		<slot
			v-else-if="isEmpty"
			name="empty"
		>
			<div
				v-if="isEmptyMessage"
				class="w-full h-full flex items-center justify-center"
			>
				<p class="text-2xl">
					{{ isEmptyMessage }}
				</p>
			</div>
		</slot>

		<slot v-else />
	</div>
</template>
