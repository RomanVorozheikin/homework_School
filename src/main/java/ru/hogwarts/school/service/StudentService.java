package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.StudentLastFive;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentService {
    Logger logger = LoggerFactory.getLogger(StudentService.class);
    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }
    public Collection<Student> getByFacultyId(Long facultyId) {
        logger.info("Was invoked method for get by faculty id students");
        return studentRepository.findByFacultyId(facultyId);
    }
    public Faculty getFacultyByStudentId(Long id) {
        logger.info("Was invoked method for faculty by student id");
        return studentRepository.findStudentById(id).get().getFaculty();
    }
    public Collection<Student> getStudents() {
        logger.info("Was invoked method for get students");
        return studentRepository.findAll();
    }

    public Collection<Student> getStudentBetweenMinMax(int min, int max) {
        logger.info("Was invoked method for between min max age student");
        return studentRepository.findByAgeBetween(min, max);
    }

    public List<Student> getAllStudentAge(int age) {
        logger.info("Was invoked method for student all age");
        return getStudents().stream()
                .filter(s -> s.getAge() == age)
                .collect(Collectors.toList());
    }

    public Student createStudent(Student student) {
        logger.info("Was invoked method for create student");
        return studentRepository.save(student);
    }

    public Student findStudent(long id) {
        logger.info("Was invoked method for find student");
        return studentRepository.findById(id).orElse(null);
    }

    public Student updateStudent(Student student) {
        logger.info("Was invoked method for update student");
        return studentRepository.save(student);
    }

    public void deleteStudent(long id) {
        logger.info("Was invoked method for delete student");
        studentRepository.deleteById(id);
    }

    public Integer getCount() {
        logger.info("Was invoked method for count students");
       return studentRepository.getCountStudent();
    }

    public Double getAvgAgeStudent() {
        logger.info("Was invoked method for avg age students");
        return studentRepository.getAvgAgeStudent();
    }

    public List<StudentLastFive> getLastFives() {
        logger.info("Was invoked method for last five students");
        return studentRepository.getLastFiveStudent();
    }
}
