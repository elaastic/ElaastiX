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

package org.elaastix.server.core.infrastructure.seed

import jakarta.persistence.EntityManager
import org.elaastix.commons.platform.wip.UnclearAuthorshipOwnership
import org.elaastix.mm.content.MarkdownContent
import org.elaastix.mm.content.MarkdownText
import org.elaastix.server.activities.response.ClosedAnswer
import org.elaastix.server.activities.response.entities.ClosedQuestionEntity
import org.elaastix.server.activities.response.entities.QuestionEntity
import org.springframework.boot.ApplicationArguments
import org.springframework.core.annotation.Order

@Seeder
@Suppress("MagicNumber")
@Order(1)
@OptIn(UnclearAuthorshipOwnership::class)
class QuestionSeeder(entityManager: EntityManager, private val userSeeder: UserSeeder) :
	AbstractSeeder(entityManager) {
	lateinit var tmpQuestion1: QuestionEntity
		protected set

	lateinit var tmpQuestion2: QuestionEntity
		protected set

	override fun run(args: ApplicationArguments) {
		tmpQuestion1 = upsert(
			id = 1UL,
			author = userSeeder.cynthia, // Actually: Randall Munroe - https://xkcd.com/1312/.
			entity = ClosedQuestionEntity(
				statement = MarkdownContent(
					"TRUE OR FALSE. Software written in Haskell is guaranteed to have no side-effects",
				),
				multiple = true,
				choices = listOf(
					MarkdownText("TRUE. Haskell is a functional programming language, and therefore pure."),
					MarkdownText("FALSE. It is impossible to write side-effect-free code."),
					MarkdownText("TRUE. No one run Haskell software, and therefore no side-effect ever occurs."),
					MarkdownText("FALSE. Haskell wraps \"impure\" logic using monadic structures."),
				),
				expectedAnswer = ClosedAnswer.Multiple(setOf(2u, 3u)),
				answerExplanation = MarkdownContent("https://xkcd.com/1312/"),
			),
		)

		tmpQuestion2 = upsert(
			id = 2UL,
			author = userSeeder.cynthia,
			entity = ClosedQuestionEntity(
				statement = MarkdownContent("What is the best multipurpose operating system?"),
				multiple = false,
				choices = listOf(
					MarkdownText("Windows"),
					MarkdownText("Linux"),
					MarkdownText("OpenBSD"),
					MarkdownText("macOS"),
				),
				expectedAnswer = ClosedAnswer.Single(2U),
				answerExplanation = MarkdownContent(
					"""
					- Unmatched security by design and by default
					- Code correctness over features (i.e. not systemd 🤢)
					- Proactive protection & privacy defaults
					  - OpenBSD pioneered and enables powerful exploit mitigation technologies like W^X
					- Proven track record: *Only two remote holes in the default install, in a heck of a long time!*
					""".trimIndent(),
				),
			),
		)
	}
}
