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

import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.gradle.ext.copyright
import org.jetbrains.gradle.ext.settings

plugins {
	id("conventions.idea")
	id("conventions.kover")

	alias(libs.plugins.detekt)
	alias(libs.plugins.versions)
}

idea {
	module {
		excludeDirs.add(file(".kotlin"))
		excludeDirs.add(file(".mise"))
	}

	project {
		settings {
			copyright {
				useDefault = "ElaasticAGPL"
				profiles {
					create("ElaasticAGPL") {
						@Suppress("HttpUrlsUsage")
						notice = """
							Elaastic / ElaastiX - formative assessment system
							Copyright (C) 2019  Université de Toulouse and Université Toulouse Capitole.
							SPDX-License-Identifier: AGPL-3.0-or-later

							This program is free software: you can redistribute it and/or modify
							it under the terms of the GNU Affero General Public License as published by
							the Free Software Foundation, either version 3 of the License, or
							(at your option) any later version.

							This program is distributed in the hope that it will be useful,
							but WITHOUT ANY WARRANTY; without even the implied warranty of
							MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
							GNU Affero General Public License for more details.

							You should have received a copy of the GNU Affero General Public License
							along with this program.  If not, see <http://www.gnu.org/licenses/>.
						""".trimIndent()
						keyword = "SPDX-License-Identifier"
					}
				}
			}
		}
	}
}

dependencies {
	detektPlugins(libs.detekt.ktlint)

	kover(project(":commons:core"))
	kover(project(":commons:spring"))
	kover(project(":commons:security"))
	kover(project(":metamodel"))
	kover(project(":server"))
}

detekt {
	parallel = true
	buildUponDefaultConfig = true
	config.setFrom(file(".config/detekt/detekt.yaml"))

	source.from(
		file("build.gradle.kts"),
		file("settings.gradle.kts"),
		file("build-logic/build.gradle.kts"),
		file("build-logic/settings.gradle.kts"),

		// Sources are manually listed to better comply with Gradle's Isolated Projects
		file("commons/core/build.gradle.kts"),
		file("commons/spring/build.gradle.kts"),
		file("commons/security/build.gradle.kts"),
		file("metamodel/build.gradle.kts"),
		file("server/build.gradle.kts"),
	)
}

tasks.withType<DependencyUpdatesTask>().configureEach {
	fun isNonStable(version: String): Boolean {
		val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
		val regex = "^[0-9,.v-]+(-r[^c])?$".toRegex()
		val isStable = stableKeyword || regex.matches(version)
		return isStable.not()
	}

	rejectVersionIf {
		isNonStable(candidate.version) && !isNonStable(currentVersion)
	}
}
