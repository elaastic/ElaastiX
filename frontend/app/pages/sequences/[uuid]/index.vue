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
import SequenceLearnerView from '~/components/SequenceLearnerView.vue'
import SequenceOwnerView from '~/components/SequenceOwnerView.vue'

definePageMeta({
	middleware: 'sequence-learner',
})

const lastPage = window.history.state.back as string

const uuid = useRoute().params.uuid as string

const { isOwner, isPending, isError, error } = useSequence(uuid)
</script>

<template>
	<USkeleton
		v-if="isPending"
		class="w-full h-1/4"
	/>
	<UError
		v-else-if="isError"
		:redirect="lastPage"
		class="w-full h-full"
		icon="i-lucide-circle-x"
		:error="error"
	/>
	<SequenceOwnerView
		v-if="!isPending && !isError && isOwner"
		:uuid="uuid"
	/>
	<SequenceLearnerView
		v-else-if="!isPending && !isError"
		:uuid="uuid"
	/>
</template>
