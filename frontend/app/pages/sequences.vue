<script setup lang="ts">
</script>

<template>
	<UDashboardPanel id="sequences">
		<template #header>
			<UDashboardNavbar :title="$t('sequences.title')">
				<template #leading>
					<UDashboardSidebarCollapse />
				</template>
			</UDashboardNavbar>
		</template>

		<template #body>
			<NuxtPage />
		</template>
	</UDashboardPanel>
</template>
