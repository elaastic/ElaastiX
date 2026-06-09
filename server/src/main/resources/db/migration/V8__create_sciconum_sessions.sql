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

CREATE TABLE sciconum_chatter_entity
(
	id         UUID                        NOT NULL,
	updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
	version    BIGINT                      NOT NULL,
	learner_id UUID                        NOT NULL,
	peering_id UUID                        NOT NULL,
	nickname   VARCHAR(255)                NOT NULL,
	CONSTRAINT pk_sciconumchatterentity PRIMARY KEY (id)
);

CREATE TABLE sciconum_learner_session_entity
(
	id                UUID                        NOT NULL,
	updated_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
	version           BIGINT                      NOT NULL,
	global_session_id UUID                        NOT NULL,
	learner_id        UUID                        NOT NULL,
	phase             VARCHAR(255)                NOT NULL,
	next_phase_at     TIMESTAMP WITHOUT TIME ZONE,
	CONSTRAINT pk_sciconumlearnersessionentity PRIMARY KEY (id)
);

CREATE TABLE sciconum_peering_entity
(
	id         UUID                        NOT NULL,
	dtype      VARCHAR(31)                 NOT NULL,
	updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
	version    BIGINT                      NOT NULL,
	session_id UUID                        NOT NULL,
	round      INTEGER                     NOT NULL,
	learner_id UUID                        NOT NULL,
	CONSTRAINT pk_sciconumpeeringentity PRIMARY KEY (id)
);

CREATE TABLE sciconum_peering_entity_responses
(
	sciconum_judge_peering_entity_id UUID NOT NULL,
	responses_id                     UUID NOT NULL
);

CREATE TABLE sciconum_session_entity
(
	id               UUID                        NOT NULL,
	updated_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
	version          BIGINT                      NOT NULL,
	assignment_id    UUID                        NOT NULL,
	sequence_id      UUID                        NOT NULL,
	current_question INTEGER                     NOT NULL,
	phase            VARCHAR(255)                NOT NULL,
	next_phase_at    TIMESTAMP WITHOUT TIME ZONE,
	CONSTRAINT pk_sciconumsessionentity PRIMARY KEY (id)
);

ALTER TABLE sciconum_session_entity
	ADD CONSTRAINT uc_0049f1a1a62c78bb4160627ee UNIQUE (assignment_id, sequence_id);

ALTER TABLE sciconum_chatter_entity
	ADD CONSTRAINT unique_nick_per_group UNIQUE (peering_id, nickname);

ALTER TABLE sciconum_chatter_entity
	ADD CONSTRAINT FK_SCICONUMCHATTERENTITY_ON_LEARNER FOREIGN KEY (learner_id) REFERENCES users (id);

ALTER TABLE sciconum_chatter_entity
	ADD CONSTRAINT FK_SCICONUMCHATTERENTITY_ON_PEERING FOREIGN KEY (peering_id) REFERENCES sciconum_peering_entity (id);

ALTER TABLE sciconum_learner_session_entity
	ADD CONSTRAINT FK_SCICONUMLEARNERSESSIONENTITY_ON_GLOBALSESSION FOREIGN KEY (global_session_id) REFERENCES sciconum_session_entity (id);

ALTER TABLE sciconum_learner_session_entity
	ADD CONSTRAINT FK_SCICONUMLEARNERSESSIONENTITY_ON_LEARNER FOREIGN KEY (learner_id) REFERENCES users (id);

ALTER TABLE sciconum_peering_entity
	ADD CONSTRAINT FK_SCICONUMPEERINGENTITY_ON_LEARNER FOREIGN KEY (learner_id) REFERENCES users (id);

ALTER TABLE sciconum_peering_entity
	ADD CONSTRAINT FK_SCICONUMPEERINGENTITY_ON_SESSION FOREIGN KEY (session_id) REFERENCES sciconum_session_entity (id);

ALTER TABLE sciconum_session_entity
	ADD CONSTRAINT FK_SCICONUMSESSIONENTITY_ON_ASSIGNMENT FOREIGN KEY (assignment_id) REFERENCES assignment_entity (id);

ALTER TABLE sciconum_session_entity
	ADD CONSTRAINT FK_SCICONUMSESSIONENTITY_ON_SEQUENCE FOREIGN KEY (sequence_id) REFERENCES sequence_entity (id);

ALTER TABLE sciconum_peering_entity_responses
	ADD CONSTRAINT fk_scipeeentres_on_response_entity FOREIGN KEY (responses_id) REFERENCES response_entity (id);

ALTER TABLE sciconum_peering_entity_responses
	ADD CONSTRAINT fk_scipeeentres_on_sciconum_judge_peering_entity FOREIGN KEY (sciconum_judge_peering_entity_id) REFERENCES sciconum_peering_entity (id);
