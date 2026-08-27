<script setup lang="ts">
interface Props {
	submitting?: boolean
	error?: string
	valuePresence: boolean
	confirmRequest: boolean
}

interface Emits {
	onSubmit: [VoidFunction]
}

defineProps<Props>()
const emit = defineEmits<Emits>()

function submit() {
	emit('onSubmit', () => {})
}
</script>

<template>
	<div class="flex justify-end items-right">
		<div class="flex flex-col items-end gap-1">
			<div
				v-if="error"
				class="text-error"
			>
				{{ error }}
			</div>
			<div class="flex justify-end">
				<UButton
					:loading="submitting"
					class="cursor-pointer disabled:cursor-not-allowed disabled:opacity-65"
					:disabled="confirmRequest || submitting"
					@click="submit"
				>
					{{
						$t(
							!valuePresence
								? "activity.responses.skip"
								: "activity.responses.submit",
						)
					}}
				</UButton>
			</div>
			<div
				v-if="confirmRequest && !submitting"
				class="text-xs text-end"
			>
				<span>
					{{
						$t(
							!valuePresence
								? "activity.responses.confirm-skip"
								: !valuePresence
									? "activity.responses.confirm-no-explanation"
									: "activity.responses.confirm-no-confidence",
						) + " "
					}}
				</span>
				<UButton
					variant="link"
					:ui="{ trailingIcon: 'size-3' }"
					class="inline-flex items-center p-0 text-xs cursor-pointer gap-1"
					trailing-icon="i-lucide-arrow-right"
					@click="submit"
				>
					{{
						$t(
							!valuePresence
								? "activity.responses.skip"
								: "activity.responses.submit",
						)
					}}
				</UButton>
			</div>
		</div>
	</div>
</template>
