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

package org.elaastix.server.scenario

import org.elaastix.commons.platform.SciconumOnly

/**
 * Hard-coded scenarios for SCICONUM experiments.
 */
@SciconumOnly
enum class SciconumScenario {
	/** Control scenario. Answer a question, and wait for the results. */
	CONTROL,

	/** Peer assessment scenario. Answer a question, then judge answers from peers, and wait for results. */
	PEER_ASSESSMENT,

	/** Peer discussion scenario. Answer a question, discuss via instant chat, and wait for results. */
	PEER_DEBATE,
}
