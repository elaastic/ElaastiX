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

package org.elaastix.server.scenario.exec.entities

import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import jakarta.validation.constraints.NotNull
import org.elaastix.commons.jpa.entity.AbstractEntity
import org.elaastix.commons.platform.debt.SciconumTechDebt
import org.elaastix.server.users.entities.UserEntity

@Entity
@Table(
	uniqueConstraints = [
		UniqueConstraint(
			name = "unique_nick_per_group",
			columnNames = ["peering_id", "nickname"],
		),
	],
)
@SciconumTechDebt
class SciconumChatterEntity(
	@NotNull
	@ManyToOne
	var learner: UserEntity,

	@NotNull
	@ManyToOne
	var peering: SciconumChatPeeringEntity,

	@NotNull
	var nickname: String,
) : AbstractEntity()
