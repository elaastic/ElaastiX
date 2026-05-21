#set($resource_name = "")
#set($s = "")
#foreach($s in $class_name.split("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])"))
	#set($resource_name = $resource_name + $s + " ")
#end
#set($resource_name = $resource_name.trim().toLowerCase())

package ${PACKAGE_NAME}.${package}

import kotlinx.serialization.Serializable
import org.elaastix.commons.data.Uuid

@Serializable
data class ${class_name}Dto(
	/** The ${resource_name}'s unique identifier. */
	val id: Uuid,
)
