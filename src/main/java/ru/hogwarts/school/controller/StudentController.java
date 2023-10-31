package ru.hogwarts.school.controller;

import liquibase.pro.packaged.S;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.StudentLastFive;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("student")
public class StudentController {
    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @GetMapping("faculty-by-student-id")
    public Faculty getFacultyByStudentId(@RequestParam Long id) {
        return service.getFacultyByStudentId(id);
    }

    @GetMapping
    public ResponseEntity<List<Student>> getAllStudentAge(@RequestParam(value = "age", required = false) int age) {
        List<Student> allStudentAge = service.getAllStudentAge(age);
        if (allStudentAge.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(allStudentAge);
    }

    @GetMapping("{id}")
    public ResponseEntity<Student> getStudentInfo(@PathVariable Long id) {
        Student student = service.findStudent(id);
        if (student == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(student);
    }

    @GetMapping("minMax")
    public ResponseEntity<Collection<Student>> getStudentBetweenMinMax(@RequestParam int min,
                                                                       @RequestParam int max) {
        Collection<Student> students = service.getStudentBetweenMinMax(min, max);
        if (students.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(students);
    }

    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        return service.createStudent(student);
    }

    @PutMapping
    public ResponseEntity<Student> updateStudent(@RequestBody Student student) {
        Student s = service.updateStudent(student);
        if (s == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(s);
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteStudent(@PathVariable Long id) {
        service.deleteStudent(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("student-count")
    public Integer getStudentCount() {
        return service.getCount();
    }

    @GetMapping("avg-student")
    public Double getAvgAge() {
        return service.getAvgAgeStudent();
    }

    @GetMapping("student-last-five")
    public List<StudentLastFive> getLastFives() {
        return service.getLastFives();
    }

    @GetMapping("start-with-A")
    public Collection<String> startWithA() {
        return service.startsWithA();
    }

    @GetMapping("avg-age-student")
    public Double avgAgeStudents() {
        return service.getAvgAgeStudentStream();
    }

    @GetMapping("sum-task4")
    public Integer sumTask4() {
        return service.sumTask4();
    }
}
