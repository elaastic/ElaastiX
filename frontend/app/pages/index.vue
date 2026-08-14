<script setup lang="ts">
const { isAuthenticated, logout } = useAuthn()
const { t } = useI18n()

const authenticatedLinks = computed(() => [{
	label: t('get-started'),
	to: 'sequences',
	trailingIcon: 'i-lucide-arrow-right',
	size: 'xl',
}, {
	label: t('login.logout'),
	icon: 'i-lucide-log-out',
	size: 'xl',
	color: 'neutral',
	variant: 'subtle',
	onClick: logout,
}])

const guestLinks = computed(() => [{
	label: t('login.login'),
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
				:title="t('home.hero-title')"
				:description="t('home.hero-description')"
				:headline="t('home.work-in-progress')"
				:links="isAuthenticated ? authenticatedLinks : guestLinks"
			/>
		</template>
	</UDashboardPanel>
</template>
