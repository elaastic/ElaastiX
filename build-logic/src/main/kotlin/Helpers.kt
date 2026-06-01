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

@file:Suppress(
	"UnusedReceiverParameter",
	"UndocumentedPublicClass",
	"UndocumentedPublicFunction",
	"UndocumentedPublicProperty",
	"FunctionOnlyReturningConstant",
	"SpellCheckingInspection",
)

import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.DependencyHandlerScope

private fun depWithVersion(dep: String, version: String?): Any =
	"$dep${version?.let { ":$version" } ?: ""}"

private fun depWithModule(prefix: String, module: String, version: String? = null): Any =
	depWithVersion(
		when (module) {
			"" -> prefix
			else -> "$prefix-$module"
		},
		version,
	)

// Extra "well-known" shortcuts for common sources

fun DependencyHandler.kotlinx(module: String, version: String? = null) =
	depWithVersion("org.jetbrains.kotlinx:kotlinx-$module", version)

fun DependencyHandler.jakarta(module: String, version: String? = null) =
	depWithVersion("jakarta.$module:jakarta.$module-api", version)

object SpringDependencyShortcuts {
	object SpringStarterShortcut {
		operator fun invoke(module: String = "", version: String? = null): Any =
			depWithModule("org.springframework.boot:spring-boot-starter", module, version)

		fun test(module: String = "", version: String? = null): Any =
			depWithVersion("${invoke(module)}-test", version)
	}

	operator fun invoke(module: String, version: String? = null): Any =
		depWithModule("org.springframework:spring", module, version)
	fun data(module: String, version: String? = null): Any =
		depWithModule("org.springframework.data:spring-data", module, version)
	fun security(module: String, version: String? = null): Any =
		depWithModule("org.springframework.security:spring-security", module, version)
	fun boot(module: String = "", version: String? = null): Any =
		depWithModule("org.springframework.boot:spring-boot", module, version)

	val starter = SpringStarterShortcut
}

val DependencyHandler.spring: SpringDependencyShortcuts
	get() = SpringDependencyShortcuts

/**
 * Helper for adding a Spring Boot starter to the dependencies.
 *
 * @param starter The name of the starter
 * @param mainTarget The target for the main dependency. Set to `null` to not add to the default configuration.
 * @param testTarget The target for the test dependency. Set to `null` to not add to the test configuration.
 */
fun DependencyHandlerScope.springBootStarter(
	starter: String,
	mainTarget: String? = "implementation",
	testTarget: String? = "testImplementation",
) {
	mainTarget?.let { add(it, spring.starter(starter)) }
	testTarget?.let { add(it, spring.starter.test(starter)) }
}

fun DependencyHandlerScope.testSpringBootStarter(starter: String) = springBootStarter(starter, mainTarget = null)

internal fun VersionCatalog.version(id: String) = findVersion(id).get().toString()
internal fun DependencyHandlerScope.bom(specifier: Any) = add("implementation", enforcedPlatform(specifier))
