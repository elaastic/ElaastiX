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
const { sequences, isPending } = useSequences()
</script>

<template>
	<div class="w-full h-full p-4">
		<div
			v-if="!isPending && sequences?.length === 0"
			class="w-full h-full flex items-center justify-center"
		>
			<p class="text-2xl">
				{{ $t("sequence.noSequenceAssociated") }}
			</p>
		</div>
		<div
			v-else
			class="grid grid-cols-[repeat(auto-fill,minmax(300px,1fr))] gap-4"
		>
			<div
				v-if="isPending"
				class="w-full h-full"
			>
				<USkeleton
					v-for="value in Array.from({ length: 5 }, (_, i) => i + 1)"
					:key="value"
					class="w-full h-12"
				/>
			</div>
			<UPageCard
				v-for="sequence in sequences"
				v-else
				:key="sequence.uuid"
				title="Sequence"
				:description="sequence.uuid"
				class="w-full"
				:to="`/sequences/${sequence.uuid}`"
			/>
		</div>
	</div>
</template>
