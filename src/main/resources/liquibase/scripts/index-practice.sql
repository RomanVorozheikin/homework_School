--liquibase formatted sql

--changeset roma:1
CREATE INDEX student_name_index ON student (name);

--changeset roma:2
CREATE INDEX faculty_nc_idx ON faculty (name, color);