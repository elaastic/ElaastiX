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
import type { AuthFormField, FormSubmitEvent } from '@nuxt/ui'
import * as v from 'valibot'

definePageMeta({
	layout: false,
})

const toast = useToast()

const fields: AuthFormField[] = [{
	name: 'login',
	type: 'text',
	label: 'Login',
	placeholder: 'Enter your login',
	required: true,
	defaultValue: '',
}, {
	name: 'password',
	label: 'Password',
	type: 'password',
	placeholder: 'Enter your password',
	required: true,
	defaultValue: '',
}, {
	name: 'remember',
	label: 'Remember me',
	type: 'checkbox',
}]

const providers = [{
	label: 'Google',
	icon: 'i-simple-icons-google',
	onClick: () => {
		toast.add({ title: 'Google', description: 'Login with Google' })
	},
}, {
	label: 'GitHub',
	icon: 'i-simple-icons-github',
	onClick: () => {
		toast.add({ title: 'GitHub', description: 'Login with GitHub' })
	},
}]

const LoginSchema = v.object({
	login: v.pipe(
		v.string(),
		v.nonEmpty('Please enter your login'),
		v.maxLength(128),
	),
	// Don't impose length/complexity constraints on the current password
	// It may not be up to requirements (e.g. set before policy changes, bypassed via internal systems, ...).
	password: v.pipe(
		v.string(),
		v.nonEmpty('Please enter your password'),
		v.maxLength(512),
	),
})

type LoginData = v.InferInput<typeof LoginSchema>

function onSubmit(payload: FormSubmitEvent<LoginData>) {
	console.log('Submitted', payload)
}
</script>

<template>
	<div class="min-h-svh flex flex-col items-center justify-center gap-4 p-4">
		<UPageCard class="w-full max-w-md">
			<UAuthForm
				:schema="LoginSchema"
				title="Login"
				description="Enter your credentials to access your account."
				icon="i-lucide-user"
				:fields="fields"
				:providers="providers"
				@submit="onSubmit"
			/>
		</UPageCard>
		<VersionAndLegal class="w-full max-w-md" />
	</div>
</template>
