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

ALTER TABLE sequence_entity_sciconum_questions
	ADD sciconum_questions_order INTEGER;

ALTER TABLE assignment_entity_sequences
	ADD sequences_order INTEGER;

UPDATE sequence_entity_sciconum_questions
	SET sciconum_questions_order = 0
	WHERE sciconum_questions_order IS NULL;

UPDATE assignment_entity_sequences
	SET sequences_order = 0
	WHERE sequences_order IS NULL;

ALTER TABLE assignment_entity_sequences
	ADD CONSTRAINT pk_assignmententity_sequences PRIMARY KEY (assignment_entity_id, sequences_order);

ALTER TABLE sequence_entity_sciconum_questions
	ADD CONSTRAINT pk_sequenceentity_sciconumquestions PRIMARY KEY (sciconum_sequence_entity_id, sciconum_questions_order);
