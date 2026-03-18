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
import * as v from 'valibot'
import type { FormSubmitEvent, SelectItem } from '@nuxt/ui'

const GenderEnum = {
	M: 'M',
	F: 'F',
	X: 'X',
} as const

const genderSelectOptions: SelectItem[] = [
	{ label: 'Unset / Prefer not to say', value: null },
	{ label: 'Male', value: GenderEnum.M },
	{ label: 'Female', value: GenderEnum.F },
	{ label: 'Non-binary', value: GenderEnum.X },
]

const ProfileSchema = v.object({
	firstName: v.pipe(
		v.string(),
		v.nonEmpty('Please enter your first name'),
		v.maxLength(64),
	),
	lastName: v.pipe(
		v.string(),
		v.nonEmpty('Please enter your last name'),
		v.maxLength(64),
	),
	username: v.pipe(
		v.string(),
		v.nonEmpty('Please enter your username'),
		v.maxLength(32),
	),
	email: v.pipe(
		v.string(),
		v.nonEmpty('Please enter your email'),
		v.email(),
		v.maxLength(64),
	),
	gender: v.nullable(v.enum(GenderEnum)),
})

type ProfileSchema = v.InferInput<typeof ProfileSchema>

const profile = reactive<Partial<ProfileSchema>>({
	firstName: 'Cynthia',
	lastName: 'Rey',
	username: 'cyyynthia',
	email: 'cynthia.rey@irit.fr',
	gender: GenderEnum.F,
})

const toast = useToast()
async function onSubmit(event: FormSubmitEvent<ProfileSchema>) {
	toast.add({
		title: 'Success',
		description: 'Your settings have been updated.',
		icon: 'i-lucide-check',
		color: 'success',
	})
	console.log(event.data)
}
</script>

<template>
	<UForm
		id="settings"
		:schema="ProfileSchema"
		:state="profile"
		@submit="onSubmit"
	>
		<UPageCard
			title="Profile"
			variant="naked"
			orientation="horizontal"
			class="mb-4"
		>
			<UButton
				form="settings"
				label="Save changes"
				color="neutral"
				type="submit"
				class="w-fit lg:ms-auto"
			/>
		</UPageCard>

		<UPageCard variant="subtle">
			<div class="flex gap-4">
				<UFormField
					name="firstName"
					label="First Name"
					required
					class="flex-1"
				>
					<UInput
						v-model="profile.firstName"
						autocomplete="off"
						class="w-full"
					/>
				</UFormField>
				<UFormField
					name="lastName"
					label="Last Name"
					required
					class="flex-1"
				>
					<UInput
						v-model="profile.lastName"
						autocomplete="off"
						class="w-full"
					/>
				</UFormField>
			</div>
			<USeparator />
			<UFormField
				name="email"
				label="Email"
				required
			>
				<UInput
					v-model="profile.email"
					type="email"
					autocomplete="off"
					class="w-full"
				/>
			</UFormField>
			<USeparator />
			<UFormField
				name="username"
				label="Username"
				required
			>
				<UInput
					v-model="profile.username"
					type="username"
					autocomplete="off"
					class="w-full"
				/>
			</UFormField>
			<USeparator />
			<UFormField
				name="gender"
				label="Gender"
			>
				<USelect
					v-model="profile.gender"
					:items="genderSelectOptions"
					class="w-full"
				/>
			</UFormField>
		</UPageCard>
	</UForm>
</template>
