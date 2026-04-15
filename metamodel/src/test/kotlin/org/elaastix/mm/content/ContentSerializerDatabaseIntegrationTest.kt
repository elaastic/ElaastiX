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

package org.elaastix.mm.content

import jakarta.persistence.Entity
import jakarta.persistence.EntityManager
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.assertj.core.api.Assertions.assertThat
import org.elaastix.commons.jpa.ElaastixHibernateAutoConfiguration
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.AutoConfigurationPackage
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Import
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.postgresql.PostgreSQLContainer

@Suppress("JpaDataSourceORMInspection", "SqlResolve")
@DataJpaTest(properties = ["spring.jpa.hibernate.ddl-auto=create"])
@Import(ContentSerializerDatabaseIntegrationTest.TransactionUtil::class, ElaastixHibernateAutoConfiguration::class)
@Transactional(propagation = Propagation.NEVER)
class ContentSerializerDatabaseIntegrationTest {
    companion object {
        @JvmField
        @Container
        @ServiceConnection
        val postgres = PostgreSQLContainer("postgres:18-alpine")

        class TestRichContent(val data: Map<String, JsonElement>) : RichContent {
            override fun toJson(): JsonElement = JsonObject(data)

            companion object Factory : RichContent.Factory {
                override fun fromJson(json: JsonElement): RichContent {
                    require(json is JsonObject) {
                        "Invalid JsonElement (expected JsonObject got ${json::class.simpleName})"
                    }

                    return TestRichContent(json)
                }
            }
        }
    }

    @Autowired
    lateinit var em: EntityManager

    @Autowired
    lateinit var tu: TransactionUtil

    @Test
    fun `saves to the postgres server via JPA successfully`() {
        val data = mapOf(
            "some" to JsonPrimitive("data"),
            "number" to JsonPrimitive(0),
            "bool" to JsonPrimitive(true),
            "obj" to JsonObject(
                mapOf(
                    "wow" to JsonPrimitive("meow"),
                ),
            ),
        )

        // Sanity check: DDL is correct
        val typ = assertDoesNotThrow {
            tu.runWithTransaction {
                em.createNativeQuery(
                    "SELECT data_type FROM information_schema.columns " +
                        "WHERE table_name = 'test_entity' AND column_name = 'content'",
                ).singleResult
            }
        }

        assertThat(typ)
            .isNotNull
            .isInstanceOf(String::class.java)
            .isEqualTo("jsonb")

        // Validate that saving the entity works

        val id = assertDoesNotThrow {
            tu.runWithTransaction {
                val entity = TestEntity(content = TestRichContent(data))
                em.persist(entity)

                em.entityManagerFactory.persistenceUnitUtil.getIdentifier(entity) as? Long
            }
        }.also { assertThat(it).isNotNull }!!

        // Validate that retrieving the entity works

        val entity = assertDoesNotThrow {
            tu.runWithTransaction {
                em.find(TestEntity::class.java, id)
            }
        }.also { assertThat(it).isNotNull }!!
        val content = entity.content as TestRichContent
        assertThat(content.data).isEqualTo(data)

        // Validate that a native query works as expected

        val res = assertDoesNotThrow {
            tu.runWithTransaction {
                em.createNativeQuery("SELECT content->'d'->'obj'->>'wow' FROM test_entity WHERE id = :id")
                    .apply { setParameter("id", id) }
                    .singleResult
            }
        }

        assertThat(res)
            .isNotNull
            .isInstanceOf(String::class.java)
            .isEqualTo("meow")
    }

    @Component
    class TransactionUtil {
        @Transactional
        fun <X> runWithTransaction(f: () -> X): X = f()
    }

    @Entity
    @Table(name = "test_entity")
    class TestEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        @Suppress("unused")
        var id: Long? = null,

        @JdbcTypeCode(SqlTypes.JSON)
        var content: RichContent,
    )

    @SpringBootConfiguration
    @AutoConfigurationPackage
    class Config
}
