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

CREATE TABLE sciconum_chat_peering_entity
(
	id                  UUID                        NOT NULL,
	updated_at          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
	version             BIGINT                      NOT NULL,
	scenario_session_id UUID                        NOT NULL,
	session_round       INTEGER                     NOT NULL,
	CONSTRAINT pk_sciconumchatpeeringentity PRIMARY KEY (id)
);

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

CREATE TABLE sciconum_judge_peering_entity
(
	id                 UUID                        NOT NULL,
	updated_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
	version            BIGINT                      NOT NULL,
	learner_session_id UUID                        NOT NULL,
	session_round      INTEGER                     NOT NULL,
	CONSTRAINT pk_sciconumjudgepeeringentity PRIMARY KEY (id)
);

CREATE TABLE sciconum_judge_peering_entity_responses
(
	sciconum_judge_peering_entity_id UUID NOT NULL,
	responses_id                     UUID NOT NULL
);

CREATE TABLE sciconum_learner_session_entity
(
	id                  UUID                        NOT NULL,
	updated_at          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
	version             BIGINT                      NOT NULL,
	scenario_session_id UUID                        NOT NULL,
	learner_id          UUID                        NOT NULL,
	phase               VARCHAR(255)                NOT NULL,
	next_phase_at       TIMESTAMP WITHOUT TIME ZONE,
	CONSTRAINT pk_sciconumlearnersessionentity PRIMARY KEY (id)
);

CREATE TABLE sciconum_scenario_session_entity
(
	id            UUID                        NOT NULL,
	updated_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
	version       BIGINT                      NOT NULL,
	assignment_id UUID                        NOT NULL,
	sequence_id   UUID                        NOT NULL,
	current_round INTEGER                     NOT NULL,
	phase         VARCHAR(255)                NOT NULL,
	next_phase_at TIMESTAMP WITHOUT TIME ZONE,
	CONSTRAINT pk_sciconumscenariosessionentity PRIMARY KEY (id)
);

ALTER TABLE sciconum_scenario_session_entity
	ADD CONSTRAINT uc_73f406295bda0b595d035dc5e UNIQUE (assignment_id, sequence_id);

ALTER TABLE sciconum_chatter_entity
	ADD CONSTRAINT unique_nick_per_group UNIQUE (peering_id, nickname);

ALTER TABLE sciconum_chat_peering_entity
	ADD CONSTRAINT FK_SCICONUMCHATPEERINGENTITY_ON_SCENARIOSESSION FOREIGN KEY (scenario_session_id) REFERENCES sciconum_scenario_session_entity (id);

ALTER TABLE sciconum_chatter_entity
	ADD CONSTRAINT FK_SCICONUMCHATTERENTITY_ON_LEARNER FOREIGN KEY (learner_id) REFERENCES users (id);

ALTER TABLE sciconum_chatter_entity
	ADD CONSTRAINT FK_SCICONUMCHATTERENTITY_ON_PEERING FOREIGN KEY (peering_id) REFERENCES sciconum_chat_peering_entity (id);

ALTER TABLE sciconum_judge_peering_entity
	ADD CONSTRAINT FK_SCICONUMJUDGEPEERINGENTITY_ON_LEARNERSESSION FOREIGN KEY (learner_session_id) REFERENCES sciconum_learner_session_entity (id);

ALTER TABLE sciconum_learner_session_entity
	ADD CONSTRAINT FK_SCICONUMLEARNERSESSIONENTITY_ON_LEARNER FOREIGN KEY (learner_id) REFERENCES users (id);

ALTER TABLE sciconum_learner_session_entity
	ADD CONSTRAINT FK_SCICONUMLEARNERSESSIONENTITY_ON_SCENARIOSESSION FOREIGN KEY (scenario_session_id) REFERENCES sciconum_scenario_session_entity (id);

ALTER TABLE sciconum_scenario_session_entity
	ADD CONSTRAINT FK_SCICONUMSCENARIOSESSIONENTITY_ON_ASSIGNMENT FOREIGN KEY (assignment_id) REFERENCES assignment_entity (id);

ALTER TABLE sciconum_scenario_session_entity
	ADD CONSTRAINT FK_SCICONUMSCENARIOSESSIONENTITY_ON_SEQUENCE FOREIGN KEY (sequence_id) REFERENCES sequence_entity (id);

ALTER TABLE sciconum_judge_peering_entity_responses
	ADD CONSTRAINT fk_scijudpeeentres_on_response_entity FOREIGN KEY (responses_id) REFERENCES response_entity (id);

ALTER TABLE sciconum_judge_peering_entity_responses
	ADD CONSTRAINT fk_scijudpeeentres_on_sciconum_judge_peering_entity FOREIGN KEY (sciconum_judge_peering_entity_id) REFERENCES sciconum_judge_peering_entity (id);
