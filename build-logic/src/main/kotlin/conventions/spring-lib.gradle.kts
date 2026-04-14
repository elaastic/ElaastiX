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

@file:Suppress("unused") // Modern Gradle uses `by xxx`

package conventions

import org.springframework.boot.gradle.plugin.SpringBootPlugin

val libs = the<VersionCatalogsExtension>().named("libs")

plugins {
    `jvm-test-suite`
    id("conventions.java")
    id("org.springframework.boot")
    id("org.hibernate.orm") // Plugin should automatically override Hibernate's version

    kotlin("plugin.jpa")
    kotlin("plugin.spring")
}

dependencies {
    implementation(platform(SpringBootPlugin.BOM_COORDINATES))
    implementation(libs.findLibrary("kotlin.reflect").get())
}

@Suppress("UnstableApiUsage")
testing {
    suites {
        // TODO: Separate unit tests and integration tests (for Kover reporting)
        // val integrationTest by registering(JvmTestSuite::class) {
        val test by existing(JvmTestSuite::class) {
            dependencies {
                implementation(libs.findLibrary("spring.boot.test").get())
            }

            targets.configureEach {
                testTask.configure {
                    jvmArgs = listOf("-Dspring.profiles.active=develop,testing")
                }
            }
        }
    }
}

springBoot {
    buildInfo {
        excludes = setOf("time")
    }
}
