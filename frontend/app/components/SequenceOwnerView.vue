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

<script setup lang="ts">
const { uuid } = defineProps<{ uuid: string }>();

const {
	startSequence,
	pauseSequence,
	resumeSequence,
	data,
	duration,
	state,
} = useSequence(uuid);

const name = computed(
	() => data.value?.sequence.name ?? "This sequence does not exists",
);
const currentRound = computed(() => data.value?.currentRound ?? 0);
const question = computed(
	() =>
		data.value?.sequence.sciconumQuestions[currentRound.value]
			?.statement.content ?? "",
);
const phase = computed(() => data.value?.phase);

const lastingTime = ref("");
const lessThan10secForCurrentSeq = ref(false);

const timeZone = Temporal.Now.timeZoneId();

const now = ref<undefined | Temporal.Instant>();
const endingTime = ref<undefined | Temporal.ZonedDateTime>();

const interval = setInterval(() => {
	if (duration.value === undefined || duration.value === null) return;
	if (endingTime.value === undefined || endingTime.value === null) return;

	const currentTime = Temporal.Now.zonedDateTimeISO(timeZone);

	if (Temporal.ZonedDateTime.compare(currentTime, endingTime.value) >= 0) {
		return;
	}

	if (state.value === "PAUSED") {
		return;
	}

	const remaining = endingTime.value.since(currentTime, {
		largestUnit: "day",
	});

	const parts = [];
	if (remaining.days > 0) parts.push(`${remaining.days}d`);
	if (remaining.hours > 0) parts.push(`${remaining.hours}h`);
	if (remaining.minutes > 0) parts.push(`${remaining.minutes}m`);
	if (remaining.seconds > 0) parts.push(`${Math.floor(remaining.seconds)}s`);

	lastingTime.value = parts.join(" ") || "0s";

	lessThan10secForCurrentSeq.value =
		remaining.days === 0 &&
		remaining.hours === 0 &&
		remaining.minutes === 0 &&
		remaining.seconds <= 10 &&
		remaining.seconds !== 0;
}, 500);

watch(duration, () => {
	if (state.value === "END") {
		clearInterval(interval);
		lastingTime.value = "";
		lessThan10secForCurrentSeq.value = false;
		return;
	}

	now.value = Temporal.Now.instant();

	endingTime.value = now.value
		.add(duration.value!)
		.toZonedDateTimeISO(timeZone);
});
</script>

<template>
	<UPageCard
		class="w-full h-min"
		:highlight="lessThan10secForCurrentSeq"
		highlight-color="error"
	>
		<div class="flex justify-between">
			<div class="flex flex-col gap-2">
				<div class="text-xl">
					{{ name }}
				</div>
				<div v-if="lastingTime !== ''" class="text-sm text-muted -mt-2">
					{{ $t("sequence.remaining") }}
					{{ lastingTime }}
				</div>
				<div>{{ question }}</div>
			</div>
			<div class="flex flex-col items-center gap-1">
				<div>{{ phase }}</div>

				<!-- TODO: We need to update the server so that the state is never undefined -->
				<UButton
					v-if="state === 'PENDING' || state === undefined"
					icon="i-lucide-play"
					@click="startSequence"
				>
					{{ state }}
				</UButton>
				<UButton
					v-if="state === 'RUNNING'"
					icon="i-lucide-pause"
					@click="pauseSequence"
				>
					{{ state }}
				</UButton>
				<UButton
					v-if="state === 'PAUSED'"
					icon="i-lucide-play"
					@click="resumeSequence"
				>
					{{ state }}
				</UButton>
				<UButton v-if="state === 'END'">
					{{ state }}
				</UButton>
			</div>
		</div>
	</UPageCard>
</template>
