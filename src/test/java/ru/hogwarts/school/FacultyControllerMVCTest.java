package ru.hogwarts.school;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FacultyController.class)
public class FacultyControllerMVCTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacultyRepository facultyRepository;
    @MockBean
    private StudentRepository studentRepository;
    @SpyBean
    private StudentService studentService;
    @SpyBean
    private FacultyService facultyService;

    @InjectMocks
    private FacultyController facultyController;

    Student STUDENT_1 = new Student("Gordon", 24, 1L);
    Student STUDENT_2 = new Student("Bob", 25, 2L);
    Student STUDENT_3 = new Student("Jon", 26, 3L);

    Faculty FACULTY_1 = new Faculty("Non", "Red", 1L);
    Faculty FACULTY_2 = new Faculty("Stop", "White", 2L);
    Faculty FACULTY_3 = new Faculty("Weed", "Green", 3L);
    @Test
    public void testPostGetFaculty() throws Exception {
        Long id = 1L;
        String name = "Sliz";
        String color = "Gr";

        JSONObject jsonFaculty = new JSONObject();
        jsonFaculty.put("id", id);
        jsonFaculty.put("name", name);
        jsonFaculty.put("color", color);

        Faculty faculty = new Faculty();
        faculty.setId(id);
        faculty.setName(name);
        faculty.setColor(color);

        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);
        when(facultyRepository.findById(any(Long.class))).thenReturn(Optional.of(faculty));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/faculty")
                        .content(jsonFaculty.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.color").value(color));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.color").value(color));

    }

    @Test
    public void testGetFacultyColor() throws Exception {
        String color = "Red";
        when(facultyService.getAllFacultyColor(anyString())).thenReturn(List.of(FACULTY_1));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty")
                        .param("color", color))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Non"))
                .andExpect(jsonPath("$[0].color").value("Red"));
        verify(facultyService).getAllFacultyColor(color);
    }

    @Test
    public void testGetStudentsByFaculty() throws Exception {
        STUDENT_1.setFaculty(FACULTY_1);
        STUDENT_2.setFaculty(FACULTY_1);

        List<Student> STUDENTS = new ArrayList<>(List.of(STUDENT_1, STUDENT_2));

        when(studentRepository.findByFacultyId(anyLong())).thenReturn(STUDENTS);

        Long id = 1L;
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/students-by-faculty-id")
                        .param("id", String.valueOf(id))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].faculty.id").value(STUDENT_1.getFaculty().getId()))
                .andExpect(jsonPath("$[1].faculty.name").value(STUDENT_2.getFaculty().getName()));
    }
    @Test
    public void testGetFacultyByNameColor() throws Exception {
        String color= "White";
        String name = "Non";
        when(facultyRepository.findFacultyByColorIgnoreCase(anyString())).thenReturn(List.of(FACULTY_1));
        when(facultyRepository.findFacultyByNameIgnoreCase(anyString())).thenReturn(List.of(FACULTY_2));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/find-name-by-color")
                        .param("name", name)
                        .param("color", color))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value(FACULTY_2.getName()))
                .andExpect(jsonPath("$[1].color").value(FACULTY_1.getColor()));
        verify(facultyRepository, times(1)).findFacultyByColorIgnoreCase(color);
        verify(facultyRepository, times(1)).findFacultyByNameIgnoreCase(name);
    }

    @Test
    public void deleteFaculty() throws Exception {
        Long id = 1L;
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/faculty/{id}", id))
                .andExpect(status().isOk());

        verify(facultyRepository, times(1)).deleteById(id);
    }
}
