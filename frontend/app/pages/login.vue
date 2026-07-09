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
import type { FormSubmitEvent } from '@nuxt/ui'
import { useAuthnContext } from '~/composables/authenticationProvider'

const { $api } = useNuxtApp()

const { refresh } = useAuthnContext()

definePageMeta({
	layout: false,
})

const schema = v.object({
	user: v.pipe(v.string()),
})

type Schema = v.InferOutput<typeof schema>

const users = new Map([
	['Franck', '00000000-0000-0000-0000-000000000001'],
	['John', '00000000-0000-0000-0000-000000000002'],
	['Cynthia', '00000000-0000-0000-0000-000000000003'],
])
const items = ref(users.keys().toArray())
const state = reactive({
	user: items.value[0]!,
})

async function onSubmit(submission: FormSubmitEvent<Schema>) {
	const user = submission.data.user
	const id = users.get(user)
	await $api(`/v1/authn/tmp/login`, {
		method: 'POST',
		headers: {
			Authorization: `Develop ${id}`,
		},
	})

	await refresh()
	await navigateTo('/')
}
</script>

<template>
	<div class="min-h-svh flex flex-col items-center justify-center gap-4 p-4">
		<UPageCard
			:title="$t('login.login')"
			:description="$t('login.description')"
		>
			<UForm
				:schema="schema"
				:state="state"
				class="flex flex-col gap-4"
				@submit="onSubmit"
			>
				<UFormField
					:label="$t('login.user')"
					name="uuid"
				>
					<USelect
						v-model="state.user"
						:items="items"
					/>
				</UFormField>
				<UButton
					type="submit"
					class="max-inline-max"
				>
					{{ $t("login.login") }}
				</UButton>
			</UForm>
		</UPageCard>
	</div>
</template>
