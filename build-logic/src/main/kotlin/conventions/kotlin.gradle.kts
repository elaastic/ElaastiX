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

import bom
import kotlinx
import version

val libs = the<VersionCatalogsExtension>().named("libs")

plugins {
	id("conventions.java")
	id("conventions.kotlin-minimal")

	kotlin("plugin.serialization")
	id("com.google.devtools.ksp")
}

ksp {
	arg("kdoc.all-files", "true")
}

dependencies {
	bom(kotlinx("serialization-bom", version = libs.version("kotlinx-serialization")))
	implementation(kotlinx("serialization-core"))
	implementation(kotlinx("serialization-json"))
	implementation(kotlinx("serialization-cbor"))

	implementation(libs.findLibrary("springdoc.kdoc.rt").get())
	ksp(libs.findLibrary("springdoc.kdoc.ksp").get())
}
