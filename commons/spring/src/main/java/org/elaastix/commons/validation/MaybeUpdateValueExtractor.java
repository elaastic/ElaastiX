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

package org.elaastix.commons.validation;

import jakarta.validation.valueextraction.ExtractedValue;
import jakarta.validation.valueextraction.UnwrapByDefault;
import jakarta.validation.valueextraction.ValueExtractor;
import org.elaastix.commons.data.MaybeUpdate;

/**
 * Jakarta {@link ValueExtractor} unwrapping the value of {@link MaybeUpdate} objects. Validation constraints present
 * on a field of type {@link MaybeUpdate} will be applied to the inner value, if specified. If the operation is
 * {@link MaybeUpdate.Keep}, then the validation will always succeed.
 * <p>
 * This is implemented in Java, due to the fragile support of type annotations in Kotlin. See KT-19289 and KT-13228.
 * Without {@link ExtractedValue}, the validator is unable to detect the type and perform appropriate validation.
 */
@UnwrapByDefault
public final class MaybeUpdateValueExtractor implements ValueExtractor<MaybeUpdate<@ExtractedValue ?>> {
	@Override
	public void extractValues(
		final MaybeUpdate<@ExtractedValue ?> originalValue,
		final ValueExtractor.ValueReceiver receiver
	) {
		if (originalValue instanceof MaybeUpdate.Update<?> upd) {
			receiver.value(null, upd.getValue());
		}
	}
}
