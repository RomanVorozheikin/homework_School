package ru.hogwarts.school.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("faculty")
public class FacultyController {
    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping("students-by-faculty-id")
    public Collection<Student> getStudentByFacultyId(@RequestParam Long id) {
        return facultyService.getStudentByFacultyId(id);
    }


    @GetMapping("{id}")
    public ResponseEntity<Faculty> getFacultyInfo(@PathVariable Long id) {
        Faculty faculty = facultyService.findFaculty(id);
        if (faculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty);
    }

    @GetMapping("find-name-by-color")
    public ResponseEntity<List<Faculty>> findFacultyNameOrColor(@RequestParam String name,
                                                                @RequestParam String color) {
        List<Faculty> faculties = facultyService.findFacultyNameOrColor(name, color);
        if (faculties.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(faculties);
    }

    @GetMapping
    public ResponseEntity<List<Faculty>> getAllFacultyColor(@RequestParam(value = "color", required = false) String color) {
        List<Faculty> faculties = facultyService.getAllFacultyColor(color);
        if (faculties.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculties);
    }

    @PostMapping
    public Faculty createFaculty(@RequestBody Faculty faculty) {
        return facultyService.createFaculty(faculty);
    }

    @PutMapping
    public ResponseEntity<Faculty> updateFaculty(@RequestBody Faculty faculty) {
        Faculty f = facultyService.updateFaculty(faculty);
        if (f == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(f);
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteFaculty(@PathVariable Long id) {
        facultyService.deleteFaculty(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("long-faculty-name")
    public String longestFacultyName() {
        return facultyService.longestFacultyName();
    }
}
