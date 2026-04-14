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

package org.elaastix.mm.content.internal

import org.hibernate.boot.model.TypeContributions
import org.hibernate.boot.model.TypeContributor
import org.hibernate.service.ServiceRegistry

/**
 * Custom [TypeContributor] responsible for registering the package's converters to the Hibernate context.
 *
 * Implementation has to cope with Hibernate bug [HHH-20070](https://hibernate.atlassian.net/browse/HHH-20070);
 * the contributor is invoked twice, causing duplicate registration that leads to application start failure.
 *
 * The contributor will be automatically picked by Hibernate thanks to the
 * `META-INF/services/org.hibernate.boot.model.TypeContributor` file.
 */
class HibernateTypeContributor : TypeContributor {
    // Workaround for https://hibernate.atlassian.net/browse/HHH-20070
    private var initialised = false

    override fun contribute(typeContributions: TypeContributions, serviceRegistry: ServiceRegistry) {
        if (!initialised) {
            initialised = true
            typeContributions.contributeAttributeConverter(ContentJpaConverter::class.java)
        }
    }
}
