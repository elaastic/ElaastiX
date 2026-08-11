<script lang="ts" setup>
import type { NuxtError } from '#app'

const { error } = defineProps<{ error: NuxtError }>()
</script>

<template>
	<NuxtLayout name="default">
		<UDashboardPanel id="error">
			<template #header>
				<UDashboardNavbar :title="$t('error')">
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
