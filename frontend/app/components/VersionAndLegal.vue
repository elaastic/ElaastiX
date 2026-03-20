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
const { public: { version, gitHash } } = useRuntimeConfig()
</script>

<template>
	<div class="flex flex-col items-center text-toned text-xs">
		<div class="flex items-center gap-4">
			<DialogAbout>
				<UButton
					class="shrink-0 p-0 text-xs underline cursor-pointer"
					color="neutral"
					variant="link"
				>
					{{ $t('legal.about') }}
				</UButton>
			</DialogAbout>
			<NuxtLink
				to="/legal"
				class="shrink-0 underline text-muted hover:text-default transition-colors"
			>
				{{ $t('legal.notice') }}
			</NuxtLink>
		</div>
		<span class="font-mono font-thin text-[0.8em]">
			{{ version }} ({{ gitHash }})
		</span>
	</div>
</template>
