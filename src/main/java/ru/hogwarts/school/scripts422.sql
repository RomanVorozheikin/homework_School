CREATE TABLE People (
  person_id INT PRIMARY KEY,
  name VARCHAR(255),
  age INT,
  has_license BOOLEAN
);

CREATE TABLE Cars (
  car_id INT PRIMARY KEY,
  brand VARCHAR(255),
  model VARCHAR(255),
  price DECIMAL(10, 2)
);

CREATE TABLE Ownership (
  person_id INT,
  car_id INT,
  FOREIGN KEY (person_id) REFERENCES People(person_id),
  FOREIGN KEY (car_id) REFERENCES Cars(car_id)
);