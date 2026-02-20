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

package org.elaastix.commons.jpa

import io.hypersistence.utils.spring.repository.BaseJpaRepository
import org.springframework.data.repository.NoRepositoryBean
import kotlin.uuid.Uuid

/**
 * Specialised Repository type for use in all Elaastix projects.
 * Wrapper around Hypersistence Utils's [BaseJpaRepository].
 *
 * @param T Type of entities managed by the repository. Must be a subclass of [AbstractEntity].
 */
@NoRepositoryBean
interface ElaastixRepository<T : AbstractEntity> : BaseJpaRepository<T, Uuid> {
    /**
     * Helper to use Kotlin nullability instead of Java's `Optional`, which is more idiomatic.
     */
    fun findByIdOrNull(id: Uuid): T? = findById(id).orElse(null)
}
