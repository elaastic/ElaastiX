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

val libs = the<VersionCatalogsExtension>().named("libs")

group = rootProject.group
version = project.findProperty("VERSION") ?: "0.0.0-SNAPSHOT"
val revision = project.findProperty("REVISION") ?: ""

plugins {
    id("java")
}

java {
    val jdkVersion = libs.findVersion("jdk").get().requiredVersion

    toolchain {
        languageVersion.set(
            JavaLanguageVersion.of(jdkVersion),
        )
    }

    sourceCompatibility = JavaVersion.toVersion(jdkVersion)
    targetCompatibility = JavaVersion.toVersion(jdkVersion)
}

tasks.withType<Jar> {
    metaInf {
        from("${rootProject.projectDir}/LICENSE")
    }
}

tasks.jar {
    manifest {
        attributes(
            mapOf(
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version,
                "Implementation-Vendor" to "IRIT TALENT team",
                "Bundle-License" to "AGPL-3.0-or-later",
                "Git-Revision" to revision,
            ),
        )
    }
}

tasks.register("resolveDependencies") {
    group = "build"

    configurations.compileClasspath.configure { resolve() }
    configurations.runtimeClasspath.configure { resolve() }
}

tasks.register("resolveTestDependencies") {
    group = "build"

    configurations.testCompileClasspath.configure { resolve() }
    configurations.testRuntimeClasspath.configure { resolve() }
}
