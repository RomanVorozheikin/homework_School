--liquibase formatted sql

--chengeset roma:1
CREATE INDEX student_name_index ON student (name);

--chengeset roma:2
CREATE INDEX faculty_nc_idx ON faculty (name, color);