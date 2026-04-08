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

import dev.detekt.gradle.Detekt
import org.jetbrains.gradle.ext.copyright
import org.jetbrains.gradle.ext.settings

val nonKotlinProjects = listOf(":frontend")
val isKotlinProject = { p: Project -> p.path !in nonKotlinProjects }

plugins {
    id("conventions.idea")

    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlin.kover)
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

    kover(project(":commons"))
    kover(project(":metamodel"))
    kover(project(":server"))
}

detekt {
    parallel = true
    buildUponDefaultConfig = true
    config.setFrom("./.config/detekt/detekt.yaml")

    source.from(
        file("build.gradle.kts"),
        file("settings.gradle.kts"),
        file("build-logic/src"),
        file("build-logic/build.gradle.kts"),
        file("build-logic/settings.gradle.kts"),
        subprojects
            .filter(isKotlinProject)
            .flatMap { listOf(it.file("src"), it.file("build.gradle.kts"), it.file("settings.gradle.kts")) },
    )
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = libs.versions.jdk
    autoCorrect = project.hasProperty("detekt.fix")

    if (project.hasProperty("detekt.only")) {
        setSource(
            project.property("detekt.only").toString()
                .split(";;")
                .map { file(it) },
        )

        // From Detekt's documentation
        val typeResolutionEnabled = !classpath.isEmpty
        if (typeResolutionEnabled && project.hasProperty("precommit")) {
            // We must exclude kts files from pre-commit hook to prevent detekt from crashing
            // This is a workaround for the https://github.com/detekt/detekt/issues/5501
            exclude("*.gradle.kts")
        }
    }

    exclude("**/resources/**", "**/build/**", "**/generated/**")

    reports {
        sarif.required = true
    }
}

kover {
    reports {
        filters {
            excludes {
                annotatedBy("org.elaastix.commons.platform.ExcludeFromCoverage")
            }
        }
    }
}
