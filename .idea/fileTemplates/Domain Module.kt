#set($resource_name = "")
#set($s = "")
#foreach($s in $class_name.split("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])"))
	#set($resource_name = $resource_name + $s + " ")
#end
#set($resource_name = $resource_name.trim().toLowerCase())

package ${PACKAGE_NAME}.${package}

import kotlinx.serialization.Serializable
import org.elaastix.commons.data.Uuid
import org.springframework.data.web.PagedModel
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("${base_url}")
class ${class_name}Controller {
	/**
	 * Create a new ${resource_name}.
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	fun create${class_name}(@RequestBody body: Create${class_name}Dto): ${class_name}Dto {
		TODO("Not yet implemented")
	}

	/**
	 * Get all ${resource_name}s.
	 */
	@GetMapping
	fun getAll${class_name}s(): PagedModel<${class_name}Dto> {
		TODO("Not yet implemented")
	}

	/**
	 * Get a ${resource_name}.
	 */
	@GetMapping("{id}")
	fun get${class_name}(id: Uuid): ${class_name}Dto {
		TODO("Not yet implemented")
	}

	/**
	 * Update a ${resource_name}.
	 */
	@PatchMapping("{id}")
	fun update${class_name}(id: Uuid, @RequestBody body: Update${class_name}Dto): ${class_name}Dto {
		TODO("Not yet implemented")
	}

	/**
	 * Delete a ${resource_name}.
	 */
	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	fun delete${class_name}(id: Uuid) {
		TODO("Not yet implemented")
	}

	/** Request payload for creating a ${resource_name}. */ 
	@Serializable
	data class Create${class_name}Dto(
		foo: Nothing, // TODO
	)

	/** Request payload for updating a ${resource_name}. */
	@Serializable
	data class Update${class_name}Dto(
		foo: Nothing, // TODO
	)
}
