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

import jakarta.validation.Valid
import jakarta.validation.ValidationException
import org.elaastix.commons.data.Uuid
import org.elaastix.commons.exceptions.ResourceNotFoundException
import org.elaastix.commons.orNotFound
import ${PACKAGE_NAME}.${class_name_plural.toLowerCase()}.dto.${class_name}Dto
import ${PACKAGE_NAME}.${class_name_plural.toLowerCase()}.dto.Create${class_name}Dto
import ${PACKAGE_NAME}.${class_name_plural.toLowerCase()}.dto.Update${class_name}Dto
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedModel
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ${class_name}Service(private val ${camel_class_name}Repository: ${class_name}Repository) {
	companion object {
		/** Maps ${a_or_an} [${class_name}Entity] to ${a_or_an} [${class_name}Dto]. */
		fun ${class_name}Entity.toDto(): ${class_name}Dto =
			${class_name}Dto(
				id = id,
			)
	}

	/**
	 * Gets all ${resource_name_pl}, with pagination.
	 *
	 * @param pageable Spring's [Pageable] object for pagination.
	 * @return The result page.
	 */
	@Transactional(readOnly = true)
	fun getAll${class_name}s(pageable: Pageable): PagedModel<${class_name}Dto> {
		TODO("Not yet implemented")
	}

	/**
	 * Retrieves ${a_or_an} ${resource_name} by its ID.
	 *
	 * @param id The ID to fetch.
	 * @throws ResourceNotFoundException if the resource does not exist.
	 * @return The requested ${resource_name}.
	 */
	@Transactional(readOnly = true)
	fun get${class_name}(id: Uuid): ${class_name}Dto {
		return ${camel_class_name}Repository.findById(id).orNotFound().toDto()
	}

	/**
	 * Creates a new ${resource_name}.
	 *
	 * @param dto The creation payload. Will be validated.
	 * @throws ValidationException if the [dto] is invalid.
	 * @return The created entity.
	 */
	@Transactional
	fun create${class_name}(@Valid dto: Create${class_name}Dto): ${class_name}Dto {
		val entity = ${camel_class_name}Repository.persist(
			${class_name}Entity(
				// TODO: implement
			)
		)

		return entity.toDto()
	}

	/**
	 * Updates ${a_or_an} ${resource_name} by its ID.
	 *
	 * @param id The ID to update.
	 * @param dto The update payload. Will be validated.
	 * @throws ResourceNotFoundException if the resource [id] does not exist.
	 * @throws ValidationException if the [dto] is invalid.
	 * @return The updated entity.
	 */
	@Transactional
	fun update${class_name}(id: Uuid, @Valid dto: Update${class_name}Dto): ${class_name}Dto {
		val entity = ${camel_class_name}Repository.findByIdAndUpdate(id) {
			// TODO: implement
		}

		return entity.orNotFound().toDto()
	}

	/**
	 * Deletes ${a_or_an} ${resource_name} by its ID.
	 *
	 * @param id The ID to delete.
	 * @throws DataIntegrityViolationException if a foreign key constraint fails.
	 */
	@Transactional
	fun delete${class_name}(id: Uuid) {
		${camel_class_name}Repository.deleteById(id)
	}
}
