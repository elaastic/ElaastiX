package org.elaastix.engine.manager

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Endpoints for retrieving available resources offered by the engine.
 */
@RestController
@RequestMapping("/catalogue")
class CatalogueController {
	/**
	 * Retrieves the activities registered in the engine and returns them to the user.
	 *
	 * @return List of activities available to use.
	 */
	@GetMapping("/activities")
	fun listActivities(): Nothing = TODO()
}
