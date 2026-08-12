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

ALTER TABLE sequence_entity
	ADD creator_id UUID;

UPDATE sequence_entity
	SET creator_id = owner_id
	WHERE creator_id IS NULL;

ALTER TABLE sequence_entity
	ALTER COLUMN creator_id SET NOT NULL;

ALTER TABLE sequence_entity
	ADD CONSTRAINT FK_SEQUENCEENTITY_ON_CREATOR FOREIGN KEY (creator_id) REFERENCES users (id);

ALTER TABLE assignment_entity
	ADD owner_id UUID;

UPDATE assignment_entity
	SET owner_id = creator_id
	WHERE owner_id IS NULL;

ALTER TABLE assignment_entity
	ALTER COLUMN owner_id SET NOT NULL;

ALTER TABLE assignment_entity
	ADD CONSTRAINT FK_ASSIGNMENTENTITY_ON_OWNER FOREIGN KEY (owner_id) REFERENCES users (id);
