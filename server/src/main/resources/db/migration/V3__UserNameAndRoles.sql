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

-- Acceptable at this stage, we'll likely squash migrations anyway.
TRUNCATE TABLE users CASCADE;

ALTER TABLE users
	ADD first_name VARCHAR(256);

ALTER TABLE users
	ADD last_name VARCHAR(256);

ALTER TABLE users
	ADD roles TEXT[];

ALTER TABLE users
	ALTER COLUMN first_name SET NOT NULL;

ALTER TABLE users
	ALTER COLUMN last_name SET NOT NULL;

ALTER TABLE users
	DROP COLUMN is_administrator;

ALTER TABLE users
	DROP COLUMN is_writer_mode_enabled;
