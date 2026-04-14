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

val libs = the<VersionCatalogsExtension>().named("libs")

plugins {
    `jvm-test-suite`
    id("conventions.java")
    id("org.jetbrains.kotlinx.kover")
}

configurations.configureEach {
    exclude(group = "org.mockito", module = "mockito-core")
    exclude(group = "org.mockito", module = "mockito-junit-jupiter")
    exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
}

@Suppress("UnstableApiUsage")
testing {
    suites {
        withType<JvmTestSuite>().configureEach {
            useJUnitJupiter(libs.findVersion("junit").get().requiredVersion)

            dependencies {
                implementation(project())
                implementation(libs.findLibrary("assertj").get())
            }
        }

        val test by existing(JvmTestSuite::class) {
            dependencies {
                implementation(libs.findLibrary("mockk").get())
                implementation(libs.findLibrary("mockk.bdd").get())
            }
        }
    }
}

kover {
    useJacoco() // https://github.com/Kotlin/kotlinx-kover/issues/720
    // TODO: Separate reports for unit tests vs integration tests

    reports {
        filters {
            excludes {
                annotatedBy("org.elaastix.commons.platform.ExcludeFromCoverage")
            }
        }

        total {
            xml {
                onCheck.set(true)
            }
        }
    }
}
