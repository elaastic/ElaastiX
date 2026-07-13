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

package org.elaastix.commons.ws

import org.apache.commons.logging.LogFactory
import org.elaastix.commons.data.Uuid
import org.elaastix.commons.gc.GarbageCollector
import org.elaastix.commons.withTimeMeasurement
import org.springframework.web.socket.WebSocketSession
import kotlin.time.Clock
import kotlin.time.DurationUnit

/**
 * Class responsible for holding [WebSocketSession] and the broadcast scopes they belong to.
 *
 * A broadcast scope is defined by an arbitrary [Uuid]. In practice, it should most often be the ID of an entity that
 * represents a session learners are a part of.
 */
class WebSocketSessionHolder(private val clock: Clock) : GarbageCollector {
	typealias UserId = Uuid
	typealias BroadcastScope = Uuid

	companion object {
		private val LOGGER = LogFactory.getLog(WebSocketSessionHolder::class.java)
	}

	private val sessions = mutableMapOf<String, WebSocketSession>()
	private val userSessions = mutableMapOf<UserId, MutableSet<String>>().withDefault { mutableSetOf() }
	private val broadcastScopes = mutableMapOf<BroadcastScope, MutableSet<String>>().withDefault { mutableSetOf() }

	/**
	 * Registers a WebSocket session. Mandatory in order to assign the session to broadcast scopes.
	 * Must be unregistered via [unregisterSession] when disconnecting!
	 */
	fun registerSession(session: WebSocketSession, owner: UserId) {
		userSessions.getValue(owner).add(session.id)
		sessions[session.id] = session
	}

	/**
	 * Un-registers a WebSocket session. Failing to unregister a session is a memory leak!
	 */
	fun unregisterSession(session: WebSocketSession) {
		userSessions.values.forEach { it.remove(session.id) }
		broadcastScopes.values.forEach { it.remove(session.id) }
		sessions.remove(session.id)
	}

	/**
	 * Assigns a session to a broadcast scope.
	 * Once assigned to a broadcast scope, the session will receive events broadcast to the scope.
	 */
	fun assignToBroadcastScope(session: WebSocketSession, scope: BroadcastScope) {
		broadcastScopes.getValue(scope).add(session.id)
	}

	/**
	 * Removes a session from a broadcast scope. It will no longer receive events for the scope.
	 *
	 * Note: it is not necessarily to explicitly unassign sessions prior to unregistering them.
	 */
	fun unassignToBroadcastScope(session: WebSocketSession, scope: BroadcastScope) {
		broadcastScopes.getValue(scope).remove(session.id)
	}

	/**
	 * Disbands the broadcast scope, unsubscribing all sessions from receiving events for the scope.
	 */
	fun disbandBroadcastScope(scope: BroadcastScope) {
		broadcastScopes.remove(scope)
	}

	/**
	 * Retrieves all sessions of a given user.
	 *
	 * A user may have multiple tabs open, so there is a possibility that the number of sessions is greater than 1.
	 */
	fun getSessionsOfUser(userId: UserId) = userSessions.getValue(userId).mapNotNull { sessions[it] }

	/**
	 * Retrieves all sessions that are part of the given broadcast scope.
	 *
	 * Sessions should all be opened, but consumers should guard against stale sessions being returned in case of
	 * bugs or clients disconnecting in between the retrieval of the set and the actual use of the session object.
	 */
	fun getSessionsInBroadcastScope(scope: BroadcastScope) = broadcastScopes.getValue(scope).mapNotNull { sessions[it] }

	override fun gc() {
		LOGGER.debug("Starting garbage collection.")

		val (_, duration) = withTimeMeasurement(clock) {
			val iterator = sessions.iterator()
			val purged = mutableSetOf<String>()
			while (iterator.hasNext()) {
				val (key, session) = iterator.next()
				if (!session.isOpen) {
					LOGGER.warn("Garbage collection: WebSocket session $key is closed but was still registered.")
					iterator.remove()
					purged.add(key)
				}
			}

			if (purged.isNotEmpty()) {
				broadcastScopes.values.forEach { it.removeAll(purged) }
				LOGGER.warn("Garbage collection pruned ${purged.size} stale sessions. There may be a memory leak.")
			}

			var removedScopes = 0u
			val broadcastIterator = broadcastScopes.iterator()
			while (broadcastIterator.hasNext()) {
				val (_, item) = broadcastIterator.next()
				if (item.isEmpty()) {
					broadcastIterator.remove()
					removedScopes++
				}
			}

			if (removedScopes > 0u) {
				LOGGER.debug("Garbage collector purged $removedScopes unused scopes.")
			}

			var removedUserSets = 0u
			val userSessionsIterator = userSessions.iterator()
			while (userSessionsIterator.hasNext()) {
				val (_, item) = userSessionsIterator.next()
				if (item.isEmpty()) {
					userSessionsIterator.remove()
					removedUserSets++
				}
			}

			if (removedUserSets > 0u) {
				LOGGER.debug("Garbage collector purged $removedUserSets unused user session mapping holders.")
			}
		}

		LOGGER.debug("Garbage collection complete in ${duration.toString(DurationUnit.SECONDS, 2)} seconds.")
	}
}
