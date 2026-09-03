<script lang="ts">
import type { components } from '#open-fetch-schemas/api'
import Markdown from '~/lib/Markdown'

type Content
	= | components['schemas']['MarkdownContent']
		| components['schemas']['MarkdownText']
		| components['schemas']['PlainText']
</script>

<script setup lang="ts">
const { content } = defineProps<{ content: Content }>()

const never = (a: never): string => (a as { $type: string }).$type ?? JSON.stringify(a)
</script>

<template>
	<Markdown
		v-if="content.$type === 'Markdown'"
		:markdown="content.content"
	/>
	<Markdown
		v-else-if="content.$type === 'MarkdownInline'"
		:markdown="content.content"
		inline
	/>
	<div v-else-if="content.$type === 'PlainText'">
		{{ content.content }}
	</div>
	<div
		v-else
		class="border border-error"
	>
		Uh oh! This is not supposed to happen. Please <a href="">submit a bug report</a>.
		Unknown content type: {{ never(content) }}
	</div>
</template>
