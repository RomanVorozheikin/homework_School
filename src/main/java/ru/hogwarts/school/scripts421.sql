ALTER TABLE student
ADD CONSTRAINT age_constraint CHECK (age >= 16);

ALTER  TABLE student
ALTER COLUMN UNIQUE (name) NOT NULL;

ALTER TABLE student
ADD CONSTRAINT name_unique UNIQUE (name);

ALTER TABLE faculty
ADD CONSTRAINT name_color_unique UNIQUE (name, color);

ALTER TABLE student
ADD CONSTRAINT age_constraint CHECK (age >= 20 OR age IS NULL) DEFAULT 20;