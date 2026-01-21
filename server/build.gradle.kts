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

import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    id("conventions.idea")
    id("conventions.kotlin")
    id("conventions.spring")
    id("conventions.test")
}

dependencies {
    implementation(libs.spring.boot.actuator)
    implementation(libs.spring.boot.jpa)
    implementation(libs.spring.boot.flyway)
    implementation(libs.spring.boot.jdbc)
    implementation(libs.spring.boot.mail)
    implementation(libs.spring.boot.opentelemetry)
    implementation(libs.spring.boot.security)
    implementation(libs.spring.boot.security.oauth2)
    implementation(libs.spring.boot.thymeleaf)
    implementation(libs.spring.boot.validation)
    implementation(libs.spring.boot.webmvc)
    implementation(libs.flyway.postgresql)
    runtimeOnly(libs.jdbc.postgresql)

    testImplementation(libs.spring.boot.actuator.test)
    testImplementation(libs.spring.boot.jpa.test)
    testImplementation(libs.spring.boot.flyway.test)
    testImplementation(libs.spring.boot.jdbc.test)
    testImplementation(libs.spring.boot.mail.test)
    testImplementation(libs.spring.boot.opentelemetry.test)
    testImplementation(libs.spring.boot.security.test)
    testImplementation(libs.spring.boot.security.oauth2.test)
    testImplementation(libs.spring.boot.thymeleaf.test)
    testImplementation(libs.spring.boot.validation.test)
    testImplementation(libs.spring.boot.webmvc.test)
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
        "-Dcom.sun.management.jmxremote.port=55432",
        "-Dcom.sun.management.jmxremote.rmi.port=55432",
        "-Dcom.sun.management.jmxremote.authenticate=false",
        "-Dcom.sun.management.jmxremote.ssl=false",
        "-Djava.rmi.server.hostname=localhost",
        "-Dspring.jmx.enabled=true",
        "-Dspring.application.admin.enabled=true",
        "-Dspring.liveBeansView.mbeanDomain",
    )
}
