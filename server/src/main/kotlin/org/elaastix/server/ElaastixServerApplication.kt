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

package org.elaastix.server

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.info.License
import org.elaastix.commons.jpa.repository.ElaastixRepositoryImplFactoryBean
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@OpenAPIDefinition(
	info = Info(
		title = "ElaastiX platform API documentation",
		description = "API documentation for the ElaastiX platform",
		// termsOfService = ""
		contact = Contact(
			name = "Franck Silvestre",
			email = "franck.silvestre@irit.fr",
		),
		license = License(
			name = "GNU Affero General Public License 3.0 or later",
			url = "https://github.com/elaastic/elaastix/blob/main/LICENSE",
			identifier = "AGPL-3.0-or-later",
		),
	),
)
@SpringBootApplication
@ConfigurationPropertiesScan
// TODO: Find a more elegant solution to automatically configure it within Elaastix Commons
// Not fond of having this here, but let's avoid another time sink for now.
@EnableJpaRepositories(repositoryFactoryBeanClass = ElaastixRepositoryImplFactoryBean::class)
class ElaastixServerApplication

@Suppress("UndocumentedPublicFunction", "SpreadOperator")
fun main(args: Array<String>) {
	runApplication<ElaastixServerApplication>(*args)
}
