<script setup lang="ts">
import * as locales from '@nuxt/ui/locale'
import { initAuthnContext } from '~/composables/authenticationProvider'

const { locale } = useI18n()
initAuthnContext()
const nuxtLocale = computed(() => {
	switch (locale.value) {
		case 'fr_fr': return 'fr'
		case 'en_gb': return 'en'
		default: return locale.value
	}
})

const lang = computed(() => locales[nuxtLocale.value].code)
const dir = computed(() => locales[nuxtLocale.value].dir)

useHead({
	htmlAttrs: {
		lang,
		dir,
	},
})
</script>

<template>
	<UApp :locale="locales[nuxtLocale]">
		<NuxtLoadingIndicator />
		<NuxtRouteAnnouncer />
		<NuxtLayout>
			<NuxtPage />
		</NuxtLayout>
	</UApp>
</template>
