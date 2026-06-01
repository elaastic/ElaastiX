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

package conventions

import bom
import dev.detekt.gradle.Detekt
import kotlinx
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import version

val libs = the<VersionCatalogsExtension>().named("libs")

plugins {
	id("conventions.java")

	kotlin("jvm")
	kotlin("plugin.serialization")
	id("com.google.devtools.ksp")

	id("dev.detekt")
}

kotlin {
	val jdkVersion = libs.version("jdk")
	val kotlinVersion = libs.version("kotlin")

	compilerOptions {
		val kotlinVersion = KotlinVersion.valueOf(
			"KOTLIN_${kotlinVersion.substringBeforeLast(".").replace(".", "_")}",
		)

		freeCompilerArgs.addAll(
			"-Xjsr305=strict",
			"-Xannotation-default-target=param-property",
		)
		jvmTarget = JvmTarget.valueOf("JVM_$jdkVersion")
		languageVersion = kotlinVersion
		apiVersion = kotlinVersion

		// We do things RIGHT in this house.
		allWarningsAsErrors = true

		// We want to use Kotlin's Uuid, as it comes with built-in UUIDv7 generation,
		// and is supported out of the box by kotlinx.serialization.
		optIn.add("kotlin.uuid.ExperimentalUuidApi")
	}
}

ksp {
	arg("kdoc.all-files", "true")
}

detekt {
	parallel = true
	buildUponDefaultConfig = true

	val projectConfig = rootProject.file(".config/detekt/detekt.yaml")
	val subprojectConfig = file("detekt.yaml")

	config.setFrom(projectConfig)
	if (subprojectConfig.exists()) {
		config.setFrom(subprojectConfig)
	}
}

dependencies {
	bom(kotlin("bom", version = libs.version("kotlin")))
	bom(kotlinx("serialization-bom", version = libs.version("kotlinx-serialization")))

	implementation(kotlin("stdlib"))
	implementation(kotlinx("serialization-core"))
	implementation(kotlinx("serialization-json"))
	implementation(kotlinx("serialization-cbor"))

	implementation(libs.findLibrary("springdoc.kdoc.rt").get())
	ksp(libs.findLibrary("springdoc.kdoc.ksp").get())

	detektPlugins(libs.findLibrary("detekt.ktlint").get())
}

tasks.withType<Detekt>().configureEach {
	jvmTarget = libs.version("jdk")
	autoCorrect = project.hasProperty("detekt.fix")

	exclude("**/resources/**", "**/build/**", "**/generated/**")

	reports {
		sarif.required = true
	}
}
