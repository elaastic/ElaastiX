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

plugins {
    id("conventions.idea")
    id("conventions.kotlin")
    id("conventions.spring")
    id("conventions.test")
}

dependencies {
    implementation(project(":commons"))
    implementation(project(":metamodel"))
    implementation(libs.spring.boot.actuator)
    implementation(libs.spring.boot.jpa)
    implementation(libs.spring.boot.flyway)
    implementation(libs.spring.boot.mail)
    implementation(libs.spring.boot.opentelemetry)
    implementation(libs.spring.boot.security)
    implementation(libs.spring.boot.security.oauth2)
    implementation(libs.spring.boot.thymeleaf)
    implementation(libs.spring.boot.validation)
    implementation(libs.spring.boot.webmvc)
    implementation(libs.spring.boot.kotlinx.serialization.json)
    implementation(libs.flyway.postgresql)
    runtimeOnly(libs.jdbc.postgresql)

    testImplementation(libs.spring.boot.actuator.test)
    testImplementation(libs.spring.boot.jpa.test)
    testImplementation(libs.spring.boot.flyway.test)
    testImplementation(libs.spring.boot.mail.test)
    testImplementation(libs.spring.boot.opentelemetry.test)
    testImplementation(libs.spring.boot.security.test)
    testImplementation(libs.spring.boot.security.oauth2.test)
    testImplementation(libs.spring.boot.thymeleaf.test)
    testImplementation(libs.spring.boot.validation.test)
    testImplementation(libs.spring.boot.webmvc.test)
    testImplementation(libs.spring.boot.kotlinx.serialization.json.test)
}
