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

package org.elaastix.server.authn.tmp.lti

import kotlinx.serialization.Serializable
import org.elaastix.commons.platform.debt.SciconumTechDebt

// https://github.com/elaastic/elaastic-questions-server/blob/29dc9a8a09e9063c0252007f61486f9d11a20eab/server/src/main/kotlin/org/elaastic/auth/lti/controller/LtiLaunchData.kt
@Serializable
@SciconumTechDebt
@Suppress("all", "PropertyName", "SpellCheckingInspection")
data class LtiLaunchDataDto(
	val oauth_consumer_key: String,
	val user_id: String,

	val roles: String,
	val lis_person_name_given: String? = null,
	val lis_person_name_family: String? = null,

	val lis_person_contact_email_primary: String? = null,
	val context_id: String,
	val context_title: String,
	val resource_link_id: String,
	val resource_link_title: String = "Elaastic questions",
	val custom_assignmentid: String? = null,
	val lis_person_sourcedid: String? = null,
	val lis_person_name_full: String? = null,
	val ext_user_username: String? = null,
	val launch_presentation_locale: String? = null,
	val context_label: String? = null,
	val resource_link_description: String? = null,

	val context_type: String? = null,
	val lis_course_section_sourcedid: String? = null,
	val lis_result_sourcedid: String? = null,
	val lis_outcome_service_url: String? = null,
	val ext_lms: String? = null,
	val tool_consumer_info_product_family_code: String? = null,
	val tool_consumer_info_version: String? = null,
	val lti_version: String? = null,
	val lti_message_type: String? = null,
	val tool_consumer_instance_guid: String? = null,
	val tool_consumer_instance_name: String? = null,
	val tool_consumer_instance_description: String? = null,
	val launch_presentation_document_target: String? = null,
	val launch_presentation_return_url: String? = null,
)
