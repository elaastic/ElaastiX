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

package org.elaastix.server.assignments.event

import org.elaastix.server.assignments.AssignmentEntity
import org.elaastix.server.users.entities.UserEntity
import org.springframework.context.ApplicationEvent

/**
 * Fired when a learner is being added to an assignment.
 *
 * @param source The object on which the event initially occurred or with which the event is associated.
 * @property assignment The assignment the learner has been added to.
 * @property learner The learner who's been added to the assignment.
 */
class AssignmentJoinEvent(source: Any, val assignment: AssignmentEntity, val learner: UserEntity) :
	ApplicationEvent(source)
