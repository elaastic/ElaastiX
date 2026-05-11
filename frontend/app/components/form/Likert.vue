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

const ITEM_STRONG_NEGATIVE = 0
const ITEM_MEDIUM_NEGATIVE = 1
const ITEM_SLIGHT_NEGATIVE = 2
const ITEM_PERHAPS_NEUTRAL = 3
const ITEM_SLIGHT_POSITIVE = 4
const ITEM_MEDIUM_POSITIVE = 5
const ITEM_STRONG_POSITIVE = 6

const LABELS = [
	/* 3 */ [ ITEM_MEDIUM_NEGATIVE, ITEM_PERHAPS_NEUTRAL, ITEM_MEDIUM_POSITIVE ],
	/* 4 */ [ ITEM_STRONG_NEGATIVE, ITEM_SLIGHT_NEGATIVE, ITEM_SLIGHT_POSITIVE, ITEM_STRONG_POSITIVE ],
	/* 5 */ [ ITEM_STRONG_NEGATIVE, ITEM_MEDIUM_NEGATIVE, ITEM_PERHAPS_NEUTRAL, ITEM_MEDIUM_POSITIVE, ITEM_STRONG_POSITIVE ],
	/* 6 */ [ ITEM_STRONG_NEGATIVE, ITEM_MEDIUM_NEGATIVE, ITEM_SLIGHT_NEGATIVE, ITEM_SLIGHT_POSITIVE, ITEM_MEDIUM_POSITIVE, ITEM_STRONG_POSITIVE ],
	/* 7 */ [ ITEM_STRONG_NEGATIVE, ITEM_MEDIUM_NEGATIVE, ITEM_SLIGHT_NEGATIVE, ITEM_PERHAPS_NEUTRAL, ITEM_SLIGHT_POSITIVE, ITEM_MEDIUM_POSITIVE, ITEM_STRONG_POSITIVE ],
] as const
</script>

<script setup lang="ts">
import type { RadioGroupItem } from '@nuxt/ui'

const { points = 5, type = LikertScaleType.AGREEMENT } = defineProps<Props>()
const value = defineModel<number>()

const { t } = useI18n()

const likertItems = computed<RadioGroupItem[]>(() =>
	// SAFETY: [3,7] - 3 = [0,4]
	LABELS[points - 3]!.map((l, i) => ({
		label: t(`likert.${type}.${l}`),
		value: i,
	})),
)
</script>

<template>
	<URadioGroup
		v-model="value"
		:items="likertItems"
		orientation="horizontal"
		variant="card"
		:ui="{
			fieldset: 'grid grid-cols-[repeat(auto-fit,minmax(200px,1fr))]'
		}"
	/>
</template>
