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

CREATE TABLE cohorts
(
    id          UUID                        NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    version     BIGINT                      NOT NULL,
    cohort_type VARCHAR(255)                NOT NULL,
    name        VARCHAR(64)                 NOT NULL,
    CONSTRAINT pk_cohorts PRIMARY KEY (id)
);

CREATE TABLE cohorts_administrators
(
    abstract_cohort_entity_id UUID NOT NULL,
    administrators_id         UUID NOT NULL,
    CONSTRAINT pk_cohorts_administrators PRIMARY KEY (abstract_cohort_entity_id, administrators_id)
);

CREATE TABLE cohorts_members
(
    teacher_cohort_entity_id UUID NOT NULL,
    members_id               UUID NOT NULL,
    CONSTRAINT pk_cohorts_members PRIMARY KEY (teacher_cohort_entity_id, members_id)
);

CREATE TABLE users
(
    id         UUID                        NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    version    BIGINT                      NOT NULL,
    user_type  VARCHAR(255)                NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE cohorts_administrators
    ADD CONSTRAINT fk_cohadm_on_abstract_cohort_entity FOREIGN KEY (abstract_cohort_entity_id) REFERENCES cohorts (id);

ALTER TABLE cohorts_administrators
    ADD CONSTRAINT fk_cohadm_on_abstract_user_entity FOREIGN KEY (administrators_id) REFERENCES users (id);

ALTER TABLE cohorts_members
    ADD CONSTRAINT fk_cohmem_on_teacher_cohort_entity FOREIGN KEY (teacher_cohort_entity_id) REFERENCES cohorts (id);

ALTER TABLE cohorts_members
    ADD CONSTRAINT fk_cohmem_on_teacher_entity FOREIGN KEY (members_id) REFERENCES users (id);
