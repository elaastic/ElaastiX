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

<script lang="ts">
export enum LikertScaleType {
	AGREEMENT = 'AGREEMENT',
	CONFIDENCE = 'CONFIDENCE',
	// may be extended later with awareness, familiarity, satisfaction, importance...
}

interface Props {
	/**
	 * Number of points to show on the Likert scale.
	 *
	 * The labels presented to the user are defined as follows:
	 *
	 * |   | -3 | -2 | -1 | 0 | +1 | +2 | +3 |
	 * |---|:--:|:--:|:--:|:-:|:--:|:--:|:--:|
	 * | 3 |    | x  |    | x |    | x  |    |
	 * | 4 | x  |    | x  |   | x  |    | x  |
	 * | 5 | x  | x  |    | x |    | x  | x  |
	 * | 6 | x  | x  | x  |   | x  | x  | x  |
	 * | 7 | x  | x  | x  | x | x  | x  | x  |
	 */
	points?: 3 | 4 | 5 | 6 | 7

	/**
	 * Type of scale to show. Affects labelling.
	 */
	type?: LikertScaleType
}

const LABEL_TRUTH_TABLE = [
	// LSB = Disagree, MSB = Agree
	/* 3 */ 0b0101010,
	/* 4 */ 0b1010101,
	/* 5 */ 0b1101011,
	/* 6 */ 0b1110111,
	/* 7 */ 0b1111111,
] as const
</script>

<script setup lang="ts">
import type { RadioGroupItem } from '@nuxt/ui'

const { points = 5, type = LikertScaleType.AGREEMENT } = defineProps<Props>()
const value = defineModel<number>()

const { t } = useI18n()

const likertItems = computed(() => {
	// SAFETY: [3,7] - 3 = [0,4]
	const tt = LABEL_TRUTH_TABLE[points - 3]!

	const items: RadioGroupItem[] = []
	for (let i = 0, v = 0; i < 7; i++) {
		// noinspection JSBitwiseOperatorUsage
		if ((1 << i) & tt)
			items.push({
				label: t(`likert.${type}.${i}`),
				value: ++v,
			})
	}

	return items
})
</script>

<template>
	<URadioGroup
		v-model="value"
		:items="likertItems"
		orientation="horizontal"
		variant="card"
		:ui="{
			item: 'flex-[1_1_0px] cursor-pointer',
		}"
	/>
</template>
