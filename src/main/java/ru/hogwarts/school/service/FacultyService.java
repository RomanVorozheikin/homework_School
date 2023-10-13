package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private final StudentService studentService;
    @Autowired
    public FacultyService(FacultyRepository facultyRepository, StudentService studentService) {
        this.facultyRepository = facultyRepository;
        this.studentService = studentService;
    }

    public Collection<Student> getStudentByFacultyId(Long id) {
        return studentService.getByFacultyId(id);
    }
    public Collection<Faculty> getFaculty() {
        return facultyRepository.findAll();
    }

    public List<Faculty> getAllFacultyColor(String color) {
        return getFaculty().stream()
                .filter(faculty -> faculty.getColor().equals(color))
                .collect(Collectors.toList());
    }

    public List<Faculty> findFacultyNameOrColor(String name, String color) {
        List<Faculty> faculties = new ArrayList<>();
        faculties.addAll(facultyRepository.findFacultyByNameIgnoreCase(name));
        faculties.addAll(facultyRepository.findFacultyByColorIgnoreCase(color));
        return faculties;
    }
    public Faculty createFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    public Faculty findFaculty(Long lastId) {
        return facultyRepository.findById(lastId).orElse(null);
    }

    public Faculty updateFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    public void deleteFaculty(long id) {
        facultyRepository.deleteById(id);
    }
}
