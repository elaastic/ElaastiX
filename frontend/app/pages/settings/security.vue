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
import type { FormError } from '@nuxt/ui'

const singlePasswordSchema = v.pipe(
	v.string(),
	v.minLength(8, 'Must be at least 8 characters'),
	v.maxLength(512),
)

const PasswordSchema = v.object({
	// Don't impose length/complexity constraints on the current password.
	// It may not be up to requirements (e.g. set before policy changes, bypassed via internal systems, ...).
	current: v.pipe(
		v.string(),
		v.nonEmpty('Please enter your current password'),
		v.maxLength(512),
	),
	new: singlePasswordSchema,
})

type PasswordSchema = v.InferInput<typeof PasswordSchema>

const password = reactive<Partial<PasswordSchema>>({
	current: '',
	new: '',
})

const validate = (state: Partial<PasswordSchema>): FormError[] => {
	const errors: FormError[] = []
	if (state.current && state.new && state.current === state.new) {
		errors.push({ name: 'new', message: 'Passwords must be different' })
	}
	return errors
}
</script>

<template>
	<UPageCard
		title="Password"
		description="Confirm your current password before setting a new one."
		variant="subtle"
	>
		<UForm
			:schema="PasswordSchema"
			:state="password"
			:validate="validate"
			class="flex flex-col gap-4 max-w-xs"
		>
			<UFormField name="current">
				<UInput
					v-model="password.current"
					type="password"
					placeholder="Current password"
					class="w-full"
				/>
			</UFormField>

			<UFormField name="new">
				<UInput
					v-model="password.new"
					type="password"
					placeholder="New password"
					class="w-full"
				/>
			</UFormField>

			<UButton
				label="Update"
				class="w-fit"
				type="submit"
			/>
		</UForm>
	</UPageCard>

	<UPageCard
		title="Privacy"
		variant="subtle"
	>
		<template #description>
			You've agreed to the <ULink to="/legal#privacy">Privacy Policy</ULink> on <b>1 April 2025</b>.
		</template>
		<template #footer>
			<ULink
				to="https://www.cnil.fr/fr/mes-demarches/les-droits-pour-maitriser-vos-donnees-personnelles"
				class="relative group text-sm"
				target="_blank"
			>
				Learn more about your rights on <abbr title="Commission Nationale de l'Informatique et des Libertés">CNIL</abbr>'s
				website (fr)
				<UIcon
					name="i-lucide-arrow-up-right"
					class="size-3 absolute top-0 text-dimmed"
				/>
			</ULink>
		</template>
	</UPageCard>

	<UPageCard
		title="Account Deletion"
		description="You can delete your account here. This action is irreversible. All personal information related to this account will be deleted permanently."
		class="bg-linear-to-tl from-error/10 from-5% to-default"
		:ui="{ footer: 'flex gap-4' }"
	>
		<template #footer>
			<UButton
				label="Delete account"
				color="error"
			/>
			<UButton
				label="Request a copy of my data"
				color="neutral"
				variant="outline"
				class="ml-auto"
			/>
		</template>
	</UPageCard>
</template>
