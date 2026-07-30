<script lang="ts" setup>
import type { NuxtError } from '#app'

const { error } = defineProps<{ error: NuxtError }>()
</script>

<template>
	<NuxtLayout name="default">
		<UDashboardPanel id="Error">
			<template #header>
				<UDashboardNavbar title="Error">
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
