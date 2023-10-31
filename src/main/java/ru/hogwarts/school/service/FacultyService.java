package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger logger = LoggerFactory.getLogger(FacultyService.class);
    @Autowired
    public FacultyService(FacultyRepository facultyRepository, StudentService studentService) {
        this.facultyRepository = facultyRepository;
        this.studentService = studentService;
    }

    public Collection<Student> getStudentByFacultyId(Long id) {
        logger.debug("The method was called to get the faculty students");
        return studentService.getByFacultyId(id);
    }
    public Collection<Faculty> getFaculty() {
        logger.debug("The method was called to obtain a list of faculties");
        return facultyRepository.findAll();
    }

    public List<Faculty> getAllFacultyColor(String color) {
        logger.debug("A method was called to get faculties by color");
        return getFaculty().stream()
                .filter(faculty -> faculty.getColor().equals(color))
                .collect(Collectors.toList());
    }

    public List<Faculty> findFacultyNameOrColor(String name, String color) {
        logger.debug("A method was called to get faculties by color or name");
        List<Faculty> faculties = new ArrayList<>();
        faculties.addAll(facultyRepository.findFacultyByNameIgnoreCase(name));
        faculties.addAll(facultyRepository.findFacultyByColorIgnoreCase(color));
        return faculties;
    }
    public Faculty createFaculty(Faculty faculty) {
        logger.debug("The method to create a faculty was called");
        return facultyRepository.save(faculty);
    }

    public Faculty findFaculty(Long lastId) {
        logger.debug("A method was called to get the faculty");
        return facultyRepository.findById(lastId).orElse(null);
    }

    public Faculty updateFaculty(Faculty faculty) {
        logger.debug("A method was called to change the faculty");
        return facultyRepository.save(faculty);
    }

    public void deleteFaculty(long id) {
        logger.debug("A method was called to remove a faculty");
        facultyRepository.deleteById(id);
    }

    public String longestFacultyName() {
        logger.info("Method called longestFacultyName");
        return facultyRepository.findAll()
                .stream()
                .max((f1, f2) -> f1.getName().length() - f2.getName().length())
                .toString();
    }
}
