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
definePageMeta({
	middleware: 'sequence-learner',
})

const uuid = useRoute().params.uuid as string

const {
	isPending,
	error,
	data,
	lastingTime,
	lessThan10secForCurrentSeq,
	state,
	phase,
	question,
	name,
} = useSequence(uuid, false)
</script>

<template>
	<DataPage
		:error="error"
		:is-loading="isPending"
		:is-empty="false"
		is-empty-message=""
	>
		<template #loading>
			<USkeleton
				v-if="isPending"
				class="w-full h-1/4"
			/>
		</template>
		<SequenceLearnerView
			:data="data!"
			:lasting-time="lastingTime"
			:less-than10sec-for-current-seq="lessThan10secForCurrentSeq"
			:state="state"
			:name="name"
			:question="question"
			:phase="phase"
		/>
	</DataPage>
</template>
