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
import type { NavigationMenuItem } from '@nuxt/ui'

const links = [
	[
		{
			label: 'Account',
			icon: 'i-lucide-user',
			to: '/settings',
			exact: true,
		},
		{
			label: 'Security & Privacy',
			icon: 'i-lucide-shield',
			to: '/settings/security',
		},
	],
	[
		{
			label: 'Administration',
			icon: 'i-lucide-cog',
			to: '/settings/admin',
		},
	],
] satisfies NavigationMenuItem[][]
</script>

<template>
	<UDashboardPanel id="settings">
		<template #header>
			<UDashboardNavbar title="Settings">
				<template #leading>
					<UDashboardSidebarCollapse />
				</template>
			</UDashboardNavbar>

			<UDashboardToolbar>
				<!-- NOTE: The `-mx-1` class is used to align with the `DashboardSidebarCollapse` button here. -->
				<UNavigationMenu
					:items="links"
					highlight
					class="-mx-1 flex-1"
				/>
			</UDashboardToolbar>
		</template>

		<template #body>
			<div class="flex flex-col gap-4 sm:gap-6 w-full lg:max-w-2xl mx-auto">
				<NuxtPage />
			</div>
		</template>
	</UDashboardPanel>
</template>
