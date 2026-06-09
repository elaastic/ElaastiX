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

CREATE TABLE lms_user
(
	id          UUID                        NOT NULL,
	updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
	version     BIGINT                      NOT NULL,
	lti_user_id VARCHAR(255)                NOT NULL,
	user_id     UUID                        NOT NULL,
	CONSTRAINT pk_lmsuser PRIMARY KEY (id)
);

ALTER TABLE users
	ADD email VARCHAR(256);

UPDATE users
	SET email = 'unknown@email.invalid'
	WHERE email IS NULL;

ALTER TABLE users
	ALTER COLUMN email SET NOT NULL;

ALTER TABLE lms_user
	ADD CONSTRAINT FK_LMSUSER_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);
