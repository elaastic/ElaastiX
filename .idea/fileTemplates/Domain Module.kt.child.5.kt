#* /*
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
*/ *###
#set($resource_name = "")##
#set($s = "")##
#foreach($s in $class_name.split("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])"))##
#set($resource_name = $resource_name + $s + " ")##
#end##
#set($resource_name = $resource_name.trim().toLowerCase())##
package ${PACKAGE_NAME}.${class_name_plural.toLowerCase()}.dto

import kotlinx.serialization.Serializable
import org.elaastix.commons.data.Uuid

/**
 * Request payload for updating ${a_or_an} ${resource_name}.
 */
@Serializable
data class Update${class_name}Dto()
