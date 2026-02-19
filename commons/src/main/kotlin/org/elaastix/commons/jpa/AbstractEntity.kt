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
import org.elaastix.commons.platform.JpaImmutable
import org.hibernate.proxy.HibernateProxy
import java.util.UUID
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

/**
 * Abstract class holding common properties shared by all entities.
 * Deals with the implementation of best current practice, specifically around [equals] and [hashCode].
 *
 * All entities within Elaastix SHOULD inherit from AbstractEntity.
 */
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

    /**
     * ID of the entity. The ID is a UUID v7 as specified by [RFC 9562](https://www.rfc-editor.org/rfc/rfc9562.html).
     */
    @delegate:Transient
    val id: Uuid by lazy { _id.toKotlinUuid() }

    /**
     * Date of creation of the entity (millisecond precision).
     * Derived from the [id], as UUID v7 encodes the creation timestamp.
     *
     * For querying the creation timestamp from the database, please refer to Postgres 18's
     * [`uuid_extract_timestamp`](https://www.postgresql.org/docs/18/functions-uuid.html#FUNC_UUID_EXTRACT_TABLE).
     */
    @delegate:Transient
    val createdAt by lazy {
        @Suppress("MagicNumber")
        val timestamp = (_id.mostSignificantBits ushr 16) and 0xFFFFFFFFFFFFL
        Instant.fromEpochMilliseconds(timestamp)
    }

    /**
     * Date of last modification of the entity (millisecond precision).
     * Equal to the creation timestamp for newly created entities.
     */
    @NotNull
    var updatedAt: Instant = createdAt
        @JpaImmutable protected set

    @Version
    @NotNull
    // Actually used by JPA.
    // Spec allows use of any visibility for backing field, and we're using field access,
    // meaning there is no concern related to getter/setter visibility that's applicable.
    // https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#a19
    @Suppress("UnusedPrivateMember")
    private var version: Long? = null

    @PrePersist
    @PreUpdate
    // Actually used by JPA.
    // Spec allow lifecycle callbacks to have any visibility, and only disallows `static` and `final`.
    // https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#lifecycle-callback-methods
    @Suppress("UnusedPrivateMember")
    private fun updateTimestamp() {
        // Implemented "by hand" to cope with the fact we're not using Java native types.
        @OptIn(JpaImmutable::class)
        updatedAt = Clock.System.now()
    }

    /**
     * Checks if the entity is equal to [other].
     * Equality is based on the entity's class and identifier.
     *
     * Details of the implementation have been researched by the JPA Buddy team (now part of the JetBrains ecosystem).
     * It is specifically designed to be aware of Hibernate shenanigans, to avoid at all costs triggering side effects.
     */
    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass = other.hibernateAwareJavaClass
        val thisEffectiveClass = this.hibernateAwareJavaClass
        if (thisEffectiveClass != oEffectiveClass) return false

        // Cast is safe; verified the class type.
        return id == (other as AbstractEntity).id
    }

    /**
     * Computes the hash for the entity.
     *
     * The hash is solely determined by the ID, which is safe as we use globally unique IDs,
     * and allocate it immediately upon entity creation ensuring stability of the result hash.
     */
    final override fun hashCode(): Int = id.hashCode()

    private final val Any.hibernateAwareJavaClass: Class<*>
        get() = if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
}
