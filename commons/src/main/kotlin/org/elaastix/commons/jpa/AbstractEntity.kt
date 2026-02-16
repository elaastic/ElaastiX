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

import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import jakarta.persistence.Version
import jakarta.validation.constraints.NotNull
import org.elaastix.commons.platform.InternalDetail
import org.elaastix.commons.platform.JpaImmutable
import org.hibernate.proxy.HibernateProxy
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid

@MappedSuperclass
abstract class AbstractEntity {
    @Id
    var id: Uuid = Uuid.generateV7()
        @JpaImmutable protected set

    @NotNull
    var updatedAt: Instant = Clock.System.now()
        @JpaImmutable protected set

    @Version
    @NotNull
    @InternalDetail
    var version: Long? = null
        @JpaImmutable protected set

    @PrePersist
    @PreUpdate
    @Suppress("UnusedPrivateMember") // Actually used by JPA provider. Spec allows use of any visibility.
    private fun updateTimestamp() {
        @OptIn(JpaImmutable::class)
        updatedAt = Clock.System.now()
    }

    final override fun equals(other: Any?): Boolean {
        // Implementation researched by JPA Buddy.
        // Best current practice at the time of writing.
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass = other.hibernateAwareJavaClass
        val thisEffectiveClass = this.hibernateAwareJavaClass
        if (thisEffectiveClass != oEffectiveClass) return false

        // Cast is safe; verified the class type.
        return id == (other as AbstractEntity).id
    }

    // Since we're using universally unique identifiers, the hashcode of the ID is enough.
    final override fun hashCode(): Int = id.hashCode()

    private final val Any.hibernateAwareJavaClass: Class<*>
        get() = if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
}
