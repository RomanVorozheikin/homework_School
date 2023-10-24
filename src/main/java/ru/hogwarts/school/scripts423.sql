SELECT student.name, student.age, faculty.name
FROM student
JOIN faculty ON student.faculty_id = faculty.id;

SELECT s.*
FROM student s
JOIN avatar a ON s.id = a.student_id
