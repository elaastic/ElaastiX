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

import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

kotlin {
    jvmToolchain {
        languageVersion.set(
            JavaLanguageVersion.of(libs.versions.jdk.get())
        )
    }

    compilerOptions {
        val kotlinVersion = KotlinVersion.valueOf(
            "KOTLIN_${libs.versions.kotlin.get().substringBeforeLast(".").replace(".", "_")}"
        )

        jvmTarget.set(JvmTarget.valueOf("JVM_${libs.versions.jdk.get()}"))
        languageVersion.set(kotlinVersion)
        apiVersion.set(kotlinVersion)
        allWarningsAsErrors = true
    }
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(plugin(libs.plugins.kotlin.jvm))
    implementation(plugin(libs.plugins.kotlin.jpa))
    implementation(plugin(libs.plugins.kotlin.spring))
    implementation(plugin(libs.plugins.kotlin.serialization))
    implementation(plugin(libs.plugins.spring.boot))
    implementation(plugin(libs.plugins.hibernate))
    implementation(plugin(libs.plugins.detekt))
}

fun plugin(plugin: Provider<PluginDependency>) =
    plugin.map { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" }
