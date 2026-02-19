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

val libs = the<VersionCatalogsExtension>().named("libs")

plugins {
    id("conventions.java")
    id("org.springframework.boot")
    id("org.hibernate.orm") // Plugin should automatically override Hibernate's version

    kotlin("plugin.jpa")
    kotlin("plugin.spring")
}

allOpen {
    // https://youtrack.jetbrains.com/issue/KT-79389
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

dependencies {
    implementation(platform(SpringBootPlugin.BOM_COORDINATES))
    implementation(libs.findLibrary("kotlin.reflect").get())
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
