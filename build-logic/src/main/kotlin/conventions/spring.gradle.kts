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

import org.springframework.boot.gradle.plugin.SpringBootPlugin
import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun

val libs = the<VersionCatalogsExtension>().named("libs")

plugins {
    id("conventions.java")
    id("org.springframework.boot")
    id("org.hibernate.orm")

    kotlin("plugin.jpa")
    kotlin("plugin.spring")
}

configurations.configureEach {
    resolutionStrategy.eachDependency {
        when {
            // Override Hibernate's version
            // There's no easy way to sync the Gradle plugin's version with the version Spring uses
            // To make sure there's no issue, re-align everything based on the version in our own catalogue
            requested.group == "org.hibernate.orm" ->
                useVersion(libs.findVersion("hibernate").get().requiredVersion)
        }
    }
}

dependencies {
    implementation(platform(SpringBootPlugin.BOM_COORDINATES))

    implementation(libs.findLibrary("kotlin.reflect").get())
    implementation(libs.findLibrary("kotlinx.serialization.json").get())

    testImplementation(libs.findLibrary("spring.boot.test").get())
    developmentOnly(libs.findLibrary("spring.boot.devtools").get())
}

configurations.all {
    exclude(group = "org.mockito", module = "mockito-core")
    exclude(group = "org.mockito", module = "mockito-junit-jupiter")
    exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
}

springBoot {
    buildInfo {
        excludes = setOf("time")
    }
}

hibernate {
    enhancement {
        enableDirtyTracking = true
        enableLazyInitialization = true
    }
}

tasks.named<Test>("test") {
    jvmArgs = listOf("-Dspring.profiles.active=develop,testing")
}

tasks.named<BootJar>("bootJar") {
    archiveClassifier = "boot"
}

tasks.register<BootRun>("bootRunDebug") {
    val task = tasks.getByName<BootRun>("bootRun")

    mainClass = task.mainClass
    classpath = task.classpath
    jvmArgs = task.jvmArgs + listOf(
        // Enable JMX and RMI. They are very nitpicky about port, map it to the SAME port on the host in Docker!
        "-Dcom.sun.management.jmxremote",
        "-Dcom.sun.management.jmxremote.host=0.0.0.0",
        "-Dcom.sun.management.jmxremote.port=20177",
        "-Dcom.sun.management.jmxremote.rmi.port=20177",
        "-Dcom.sun.management.jmxremote.authenticate=false",
        "-Dcom.sun.management.jmxremote.ssl=false",
        "-Djava.rmi.server.hostname=localhost",
        "-Dspring.jmx.enabled=true",
        "-Dspring.application.admin.enabled=true",
        "-Dspring.liveBeansView.mbeanDomain",
    )
}
