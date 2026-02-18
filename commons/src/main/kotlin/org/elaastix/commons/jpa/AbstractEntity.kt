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

import jakarta.persistence.Column
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import jakarta.persistence.Transient
import jakarta.persistence.Version
import jakarta.validation.constraints.NotNull
import org.elaastix.commons.platform.InternalDetail
import org.elaastix.commons.platform.JpaImmutable
import org.hibernate.proxy.HibernateProxy
import java.util.UUID
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

@MappedSuperclass
@Suppress("AbstractClassCanBeConcreteClass") // Don't want the class to be constructible.
abstract class AbstractEntity {
    // Use of a backing field is "required", as JPA doesn't allow AttributeConverters along with `@Id`...
    // Doing it manually is an acceptable compromise; this logic is going to be reused universally.
    // Note on performance of using a new UUID as default value: this is in fact not an issue since the no-arg
    // constructor *does not initialise properties by default*! No useless generation happening when JPA constructs.
    @Id
    @Column(name = "id")
    private var _id: UUID = Uuid.generateV7().toJavaUuid()
        @JpaImmutable set

    @delegate:Transient
    val id: Uuid by lazy { _id.toKotlinUuid() }

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
        // Implemented "by hand" to cope with the fact we're not using Java native types.
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
