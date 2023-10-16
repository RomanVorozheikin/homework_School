package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.StudentLastFive;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Collection<Student> findByAgeBetween(int min, int max);

    Collection<Student> findByFacultyId(Long facultyId);

    Optional<Student> findStudentById(Long id);

    @Query(value = "SELECT COUNT(*) FROM student", nativeQuery = true)
    Integer getCountStudent();

    @Query(value = "select avg(age) from student", nativeQuery = true)
    Double getAvgAgeStudent();

    @Query(value = "SELECT * FROM student ORDER BY id desc LIMIT 5", nativeQuery = true)
    List<StudentLastFive> getLastFiveStudent();
}
