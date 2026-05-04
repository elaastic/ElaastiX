package org.elaastix.commons.openapi

import com.fasterxml.jackson.databind.type.SimpleType
import io.swagger.v3.core.converter.AnnotatedType
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.media.StringSchema
import org.elaastix.commons.data.Uuid
import org.springdoc.core.customizers.PropertyCustomizer
import org.springframework.stereotype.Component
import java.lang.reflect.Type

/**
 * OpenAPI property customiser setting the schema of well-known types such as [org.elaastix.commons.data.Uuid].
 */
@Component
class PropertyCustomiser : PropertyCustomizer {
	override fun customize(property: Schema<*>?, aType: AnnotatedType) =
		when (val t = aType.type) {
			is SimpleType -> customisePlainType(property, t.rawClass)
			else -> customisePlainType(property, t)
		}

	private fun customisePlainType(property: Schema<*>?, typ: Type) =
		when (typ) {
			Uuid::class.java ->
				StringSchema().apply {
					pattern = "[a-zA-Z0-9]{25}"
					example = property?.example ?: "02t2razan0q9kzr7gr55oi54j"
					description = property?.description
				}

			else -> property
		}
}
