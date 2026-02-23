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

import dev.detekt.gradle.Detekt
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

val libs = the<VersionCatalogsExtension>().named("libs")

plugins {
    id("conventions.java")
    id("dev.detekt")

    kotlin("jvm")
    kotlin("plugin.serialization")
}

kotlin {
    val jdkVersion = libs.findVersion("jdk").get().requiredVersion
    val kotlinVersion = libs.findVersion("kotlin").get().requiredVersion

    compilerOptions {
        val kotlinVersion = KotlinVersion.valueOf(
            "KOTLIN_${kotlinVersion.substringBeforeLast(".").replace(".", "_")}",
        )

        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
        jvmTarget.set(JvmTarget.valueOf("JVM_$jdkVersion"))
        languageVersion.set(kotlinVersion)
        apiVersion.set(kotlinVersion)

        // We do things RIGHT in this house.
        allWarningsAsErrors = true

        // We want to use Kotlin's Uuid, as it comes with built-in UUIDv7 generation,
        // and is supported out of the box by kotlinx.serialization.
        optIn.add("kotlin.uuid.ExperimentalUuidApi")
    }
}

detekt {
    parallel = true
    autoCorrect = true
    buildUponDefaultConfig = true

    config.setFrom(File(rootProject.projectDir, ".config/detekt/detekt.yaml"))
}

tasks.withType<Detekt>().configureEach {
    exclude("**/resources/**", "**/build/**", "**/generated/**")

    reports {
        sarif.required = true
    }
}

dependencies {
    implementation(platform(libs.findLibrary("kotlin.bom").get()))

    implementation(libs.findLibrary("kotlinx.serialization.core").get())
    implementation(libs.findLibrary("kotlinx.serialization.json").get())
    implementation(libs.findLibrary("kotlinx.serialization.cbor").get())

    detektPlugins(libs.findLibrary("detekt.ktlint").get())
}
