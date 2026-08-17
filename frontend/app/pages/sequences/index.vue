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
const { sequences, isPending, error } = useSequences()
</script>

<template>
	<DataPage
		:is-loading="isPending"
		:is-empty="sequences?.length === 0"
		:is-empty-message="$t('sequence.noSequenceAssociated')"
		:error="error"
	>
		<template #loading>
			<div class="card-grid">
				<USkeleton
					v-for="value in Array.from({ length: 5 }, (_, i) => i + 1)"
					:key="value"
					class="w-full h-12"
				/>
			</div>
		</template>

		<div class="card-grid">
			<template
				v-for="sequence in sequences"
				:key="sequence.uuid"
			>
				<UPageCard
					:title="sequence.sequenceName"
					:description="sequence.uuid"
					class="w-full relative"
					:to="`/sequences/${sequence.isOwner ? 'admin/' : ''}${sequence.uuid}`"
				>
					<UTooltip
						v-if="sequence.isOwner"
						text="You are the owner of this sequence"
						:delay-duration="0"
						class="z-1 cursor-context-menu"
					>
						<UBadge
							class="absolute top-2 right-2"
							icon="i-lucide-chess-queen"
							size="md"
							color="info"
							variant="outline"
						>
							{{ $t("sequence.owner") }}
						</UBadge>
					</UTooltip>
				</UPageCard>
			</template>
		</div>
	</DataPage>
</template>

<style scoped>
@reference "tailwindcss";

.card-grid {
	@apply grid grid-cols-[repeat(auto-fill,minmax(300px,1fr))] gap-4;
}
</style>
