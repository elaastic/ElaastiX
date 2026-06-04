/*
 * Elaastic / ElaastiX - formative assessment system
 * Copyright (C) 2019  Université de Toulouse and Université Toulouse Capitole.
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.elaastix.commons.validation

import jakarta.validation.Validation
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.assertj.core.api.Assertions.assertThat
import org.elaastix.commons.data.MaybeUpdate
import org.elaastix.commons.data.asUpdateOp
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class MaybeUpdateValidationTest {
	@Test
	fun innerStringValidation() {
		val validator = Validation.buildDefaultValidatorFactory().validator

		data class Test(@NotBlank @Size(max = 2) val v: MaybeUpdate<String>)
		val noop = Test(MaybeUpdate.Keep)
		val good = Test("a".asUpdateOp())
		val bad1 = Test("aaa".asUpdateOp())
		val bad2 = Test("".asUpdateOp())

		val noopVal = assertDoesNotThrow { validator.validate(noop) }
		val goodVal = assertDoesNotThrow { validator.validate(good) }
		val bad1Val = assertDoesNotThrow { validator.validate(bad1) }
		val bad2Val = assertDoesNotThrow { validator.validate(bad2) }

		assertThat(noopVal).isEmpty()
		assertThat(goodVal).isEmpty()
		assertThat(bad1Val).isNotEmpty()
		assertThat(bad2Val).isNotEmpty()
	}
}
