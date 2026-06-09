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

ALTER TABLE response_entity
	ADD grade DOUBLE PRECISION;

ALTER TABLE response_entity
	ADD max DOUBLE PRECISION;

ALTER TABLE response_entity
	ALTER COLUMN grade SET NOT NULL;

ALTER TABLE response_entity
	ALTER COLUMN max SET NOT NULL;

-- Forgotten null constraints in V2

ALTER TABLE response_entity
	ALTER COLUMN question_id SET NOT NULL;

ALTER TABLE response_entity
	ALTER COLUMN author_id SET NOT NULL;

ALTER TABLE response_entity
	ALTER COLUMN answer SET NOT NULL;

ALTER TABLE question_entity
	ALTER COLUMN statement SET NOT NULL;

ALTER TABLE question_entity
	ALTER COLUMN author_id SET NOT NULL;

ALTER TABLE question_entity
	ALTER COLUMN choices SET NOT NULL;

ALTER TABLE question_entity
	ALTER COLUMN expected_answer SET NOT NULL;
