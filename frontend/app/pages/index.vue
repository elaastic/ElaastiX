<script setup lang="ts">
const { isAuthenticated, logout } = useAuthn()
const { t } = useI18n()

const authenticatedLinks = computed(() => [{
	label: 'Get started',
	to: 'sequences',
	trailingIcon: 'i-lucide-arrow-right',
	size: 'xl',
}, {
	label: 'Log out',
	icon: 'i-lucide-log-out',
	size: 'xl',
	color: 'neutral',
	variant: 'subtle',
	onClick: logout,
}])

const guestLinks = computed(() => [{
	label: 'Login',
	to: '/login',
	trailingIcon: 'i-lucide-log-in',
	size: 'xl',
}])
</script>

<template>
	<UDashboardPanel id="home">
		<template #header>
			<UDashboardNavbar>
				<template #leading>
					<UDashboardSidebarCollapse />
				</template>
			</UDashboardNavbar>
		</template>

		<template #body>
			<UPageHero
				:title="t('home.heroTitle')"
				:description="t('home.heroDescription')"
				:headline="t('home.workInProgress')"
				:links="isAuthenticated ? authenticatedLinks : guestLinks"
			/>
		</template>
	</UDashboardPanel>
</template>
