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

package testutils

import jakarta.persistence.EntityManager
import net.datafaker.Faker
import org.elaastix.commons.jpa.entity.AbstractEntity
import org.elaastix.commons.toIsoStringSecondPrecise
import org.elaastix.commons.truncate
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.TransactionTemplate
import kotlin.time.Clock

@SpringBootTest(
	properties = [
		"elaastix.security.encryption-key=unsafe integration test key",
	],
)
@AutoConfigureMockMvc
@Suppress("SpringJavaInjectionPointsAutowiringInspection")
abstract class IntegrationTest {
	companion object {
		val FAKER = Faker()
	}

	@Autowired
	lateinit var em: EntityManager

	@Autowired
	lateinit var mvc: MockMvc

	@Autowired
	private lateinit var tx: TransactionTemplate

	private lateinit var testInfo: TestInfo

	@BeforeEach
	fun receiveTestInfo(testInfo: TestInfo) {
		this.testInfo = testInfo
	}

	fun <T> runWithTransaction(block: (TransactionStatus) -> T): T = tx.execute(block)

	fun <T : AbstractEntity> T.persist() = also { runWithTransaction { em.persist(this) } }

	fun makeRecognisableName(): String {
		// Target maximum length: 64
		// Structure: <class>#`<name>` @ <iso-time>
		// Iso time is 20 chars
		// 6 "cosmetic" chars
		// 64 - 26 = 38 usable characters
		// Split as 10 + 28 for class and name.
		val clazz = this::class.java.simpleName.truncate(10u)
		val name = testInfo.displayName.truncate(28u)
		val iso = Clock.System.now().toIsoStringSecondPrecise()

		return "$clazz#`$name` @ $iso"
	}
}
