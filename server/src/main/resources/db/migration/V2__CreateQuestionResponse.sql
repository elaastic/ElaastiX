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

CREATE TABLE question_entity
(
	id                 UUID                        NOT NULL,
	dtype              VARCHAR(31)                 NOT NULL,
	updated_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
	version            BIGINT                      NOT NULL,
	statement          JSONB,
	answer_explanation JSONB,
	author_id          UUID,
	choices            JSONB,
	multiple           BOOLEAN                     NOT NULL,
	expected_answer    JSONB,
	CONSTRAINT pk_questionentity PRIMARY KEY (id)
);

CREATE TABLE response_entity
(
	id                  UUID                        NOT NULL,
	dtype               VARCHAR(31)                 NOT NULL,
	updated_at          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
	version             BIGINT                      NOT NULL,
	question_id         UUID,
	self_explanation    JSONB,
	confidence_degree   INTEGER,
	author_id           UUID,
	amended_response_id UUID,
	answer              JSONB,
	CONSTRAINT pk_responseentity PRIMARY KEY (id)
);

ALTER TABLE question_entity
	ADD CONSTRAINT FK_QUESTIONENTITY_ON_AUTHOR FOREIGN KEY (author_id) REFERENCES users (id);

ALTER TABLE response_entity
	ADD CONSTRAINT FK_RESPONSEENTITY_ON_AMENDEDRESPONSE FOREIGN KEY (amended_response_id) REFERENCES response_entity (id);

ALTER TABLE response_entity
	ADD CONSTRAINT FK_RESPONSEENTITY_ON_AUTHOR FOREIGN KEY (author_id) REFERENCES users (id);

ALTER TABLE response_entity
	ADD CONSTRAINT FK_RESPONSEENTITY_ON_QUESTION FOREIGN KEY (question_id) REFERENCES question_entity (id);
