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

CREATE TABLE assignment_entity
(
	id           UUID                        NOT NULL,
	updated_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
	version      BIGINT                      NOT NULL,
	display_name VARCHAR(64)                 NOT NULL,
	creator_id   UUID                        NOT NULL,
	CONSTRAINT pk_assignmententity PRIMARY KEY (id)
);

CREATE TABLE assignment_entity_participants
(
	assignment_entity_id UUID NOT NULL,
	participants_id      UUID NOT NULL,
	CONSTRAINT pk_assignmententity_participants PRIMARY KEY (assignment_entity_id, participants_id)
);

CREATE TABLE assignment_entity_sequences
(
	assignment_entity_id UUID NOT NULL,
	sequences_id         UUID NOT NULL
);

ALTER TABLE assignment_entity
	ADD CONSTRAINT FK_ASSIGNMENTENTITY_ON_CREATOR FOREIGN KEY (creator_id) REFERENCES users (id);

ALTER TABLE assignment_entity_participants
	ADD CONSTRAINT fk_assentpar_on_assignment_entity FOREIGN KEY (assignment_entity_id) REFERENCES assignment_entity (id);

ALTER TABLE assignment_entity_participants
	ADD CONSTRAINT fk_assentpar_on_user_entity FOREIGN KEY (participants_id) REFERENCES users (id);

ALTER TABLE assignment_entity_sequences
	ADD CONSTRAINT fk_assentseq_on_assignment_entity FOREIGN KEY (assignment_entity_id) REFERENCES assignment_entity (id);

ALTER TABLE assignment_entity_sequences
	ADD CONSTRAINT fk_assentseq_on_sequence_entity FOREIGN KEY (sequences_id) REFERENCES sequence_entity (id);
