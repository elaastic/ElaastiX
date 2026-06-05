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
#set($camel_class_name = $class_name.substring(0, 1).toLowerCase() + $class_name.substring(1))##
#set($resource_name = "")##
#set($resource_name_pl = "")##
#set($base_url = "")##
#set($s = "")##
#foreach($s in $class_name.split("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])"))##
#set($resource_name = $resource_name + " " + $s)##
#end##
#foreach($s in $class_name_plural.split("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])"))##
#set($resource_name_pl = $resource_name_pl + " " + $s)##
#set($base_url = $base_url + "_" + $s)##
#end##
#set($resource_name = $resource_name.substring(1).toLowerCase())##
#set($resource_name_pl = $resource_name_pl.substring(1).toLowerCase())##
#set($base_url = $base_url.substring(1).toLowerCase())##
package ${PACKAGE_NAME}.${class_name_plural.toLowerCase()}

import org.elaastix.commons.data.Uuid
import ${PACKAGE_NAME}.${class_name_plural.toLowerCase()}.dto.${class_name}Dto
import ${PACKAGE_NAME}.${class_name_plural.toLowerCase()}.dto.Create${class_name}Dto
import ${PACKAGE_NAME}.${class_name_plural.toLowerCase()}.dto.Update${class_name}Dto
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedModel
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("${base_url}", version = "1+")
class ${class_name}Controller(private val ${camel_class_name}Service: ${class_name}Service) {
	/**
	 * Create ${a_or_an} ${resource_name}.
	 *
	 * @see [${class_name}Service.create${class_name}]
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	fun create${class_name}(@RequestBody body: Create${class_name}Dto): ${class_name}Dto =
		${camel_class_name}Service.create${class_name}(body)

	/**
	 * Get all ${resource_name_pl}.
	 *
	 * @see [${class_name}Service.getAll${class_name}s]
	 */
	@GetMapping
	fun getAll${class_name}s(pageable: Pageable): PagedModel<${class_name}Dto> =
		${camel_class_name}Service.getAll${class_name}s(pageable)

	/**
	 * Get ${a_or_an} ${resource_name}.
	 *
	 * @see [${class_name}Service.get${class_name}]
	 */
	@GetMapping("{id}")
	fun get${class_name}(@PathVariable id: Uuid): ${class_name}Dto =
		${camel_class_name}Service.get${class_name}(id)

	/**
	 * Update ${a_or_an} ${resource_name}.
	 *
	 * @see [${class_name}Service.update${class_name}]
	 */
	@PatchMapping("{id}")
	fun update${class_name}(@PathVariable id: Uuid, @RequestBody body: Update${class_name}Dto): ${class_name}Dto =
		${camel_class_name}Service.update${class_name}(id, body)

	/**
	 * Delete ${a_or_an} ${resource_name}.
	 *
	 * @see [${class_name}Service.delete${class_name}]
	 */
	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	fun delete${class_name}(@PathVariable id: Uuid) =
		${camel_class_name}Service.delete${class_name}(id)
}
