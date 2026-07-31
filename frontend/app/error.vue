<script lang="ts" setup>
import type { NuxtError } from '#app'

const { error } = defineProps<{ error: NuxtError }>()
</script>

<template>
	<NuxtLayout name="default">
		<UDashboardPanel :id="$t('error')">
			<template #header>
				<UDashboardNavbar :title="$t('error').charAt(0).toUpperCase() + $t('error').slice(1)">
					<template #leading>
						<UDashboardSidebarCollapse />
					</template>
				</UDashboardNavbar>
			</template>

			<template #body>
				<UError
					:error="error"
					class="w-full h-full"
					icon="i-lucide-circle-x"
				/>
			</template>
		</UDashboardPanel>
	</NuxtLayout>
</template>
