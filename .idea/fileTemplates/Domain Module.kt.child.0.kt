package ${PACKAGE_NAME}.${package}

import org.springframework.stereotype.Service

@Service
class ${class_name}Service {
	companion object {
		/** Maps a [${class_name}Entity] to a [${class_name}Dto]. */
		fun ${class_name}Entity.toDto(): ${class_name}Dto =
			${class_name}Dto(
				id = id,
			)
	}
}
