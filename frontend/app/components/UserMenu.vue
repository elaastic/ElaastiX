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
import type { DropdownMenuItem } from '@nuxt/ui'

defineProps<{
	collapsed?: boolean
}>()

const { locale, locales, setLocale } = useI18n()
const colorMode = useColorMode()

const user = ref({
	name: 'Cynthia Rey',
})

const items = computed<DropdownMenuItem[][]>(() => ([
	[
		{
			type: 'label',
			label: user.value.name,
		},
	],
	[
		{
			label: 'Settings',
			icon: 'i-lucide-settings',
			to: '/settings',
		},
		{
			label: 'Appearance',
			icon: 'i-lucide-sun-moon',
			children: [
				{
					label: 'System',
					icon: 'i-lucide-monitor',
					type: 'checkbox',
					checked: colorMode.preference === 'system',
					onSelect(e: Event) {
						e.preventDefault()
						colorMode.preference = 'system'
					},
				},
				{
					label: 'Light',
					icon: 'i-lucide-sun',
					type: 'checkbox',
					checked: colorMode.preference === 'light',
					onSelect(e: Event) {
						e.preventDefault()
						colorMode.preference = 'light'
					},
				},
				{
					label: 'Dark',
					icon: 'i-lucide-moon',
					type: 'checkbox',
					checked: colorMode.preference === 'dark',
					onSelect(e: Event) {
						e.preventDefault()
						colorMode.preference = 'dark'
					},
				},
			],
		},
		{
			label: 'Language',
			icon: 'i-lucide-languages',
			children: locales.value.map(l => ({
				label: l.name,
				type: 'checkbox',
				checked: locale.value === l.code,
				onSelect(e: Event) {
					e.preventDefault()
					setLocale(l.code)
				},
			})),
		},
	],
	[
		{
			label: 'Log out',
			icon: 'i-lucide-log-out',
			color: 'error',
		},
	],
	[{ type: 'label', class: 'flex flex-col text-center font-thin gap-0 p-0', slot: 'extra' }],
]))
</script>

<template>
	<UDropdownMenu
		:items="items"
		:content="{ align: 'center', collisionPadding: 12 }"
		:ui="{ content: collapsed ? 'w-48' : 'w-(--reka-dropdown-menu-trigger-width)' }"
	>
		<UButton
			:label="collapsed ? undefined : 'My account'"
			leading-icon="i-lucide-user-circle"
			:trailing-icon="collapsed ? undefined : 'i-lucide-chevrons-up-down'"
			color="neutral"
			variant="ghost"
			block
			:square="collapsed"
			class="data-[state=open]:bg-elevated"
			:ui="{ trailingIcon: 'text-dimmed' }"
		/>

		<template #extra>
			<VersionAndLegal />
		</template>
	</UDropdownMenu>
</template>
