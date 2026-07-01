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

CREATE TABLE chat_message_entity
(
	id         UUID  NOT NULL,
	chatter_id UUID  NOT NULL,
	message    JSONB NOT NULL,
	CONSTRAINT pk_chatmessageentity PRIMARY KEY (id)
);

ALTER TABLE sciconum_chatter_entity
	RENAME TO chatter_entity;

ALTER TABLE chatter_entity
	DROP COLUMN updated_at;

ALTER TABLE chatter_entity
	DROP COLUMN version;

ALTER TABLE chatter_entity
	DROP COLUMN peering_id;

ALTER TABLE chatter_entity
	RENAME COLUMN learner_id TO chatter_id;

CREATE TABLE sciconum_chat_peering_entity_chatters
(
	sciconum_chat_peering_entity_id UUID NOT NULL,
	chatters_id                     UUID NOT NULL,
	CONSTRAINT pk_sciconumchatpeeringentity_chatters PRIMARY KEY (sciconum_chat_peering_entity_id, chatters_id)
);
