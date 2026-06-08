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

package org.elaastix.server.authn.tmp.lti.lms

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.OneToOne
import jakarta.validation.constraints.NotNull
import org.elaastix.commons.jpa.entity.AbstractEntity
import org.elaastix.commons.platform.debt.SciconumTechDebt
import org.elaastix.server.users.entities.UserEntity

@SciconumTechDebt
@Entity
class LmsUser(
	@NotNull
	var ltiUserId: String,

	@NotNull
	@OneToOne(fetch = FetchType.EAGER)
	var user: UserEntity,
) : AbstractEntity()
