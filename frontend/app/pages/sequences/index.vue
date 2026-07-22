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
const items = [
	'0000000000000000000000001',
]

function selected(id: string) {
	navigateTo(`/sequences/${id}`)
}
</script>

<template>
	<div class="w-full h-full p-4">
		<div class="grid grid-cols-[repeat(auto-fill,minmax(400px,1fr))] gap-4">
			<UPageCard
				v-for="item in items"
				:key="item"
				title="Sequence"
				:description="item"
				class="w-max"
				@click="selected(item)"
			/>
		</div>
	</div>
</template>
