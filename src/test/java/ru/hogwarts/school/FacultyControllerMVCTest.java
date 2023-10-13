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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

import java.util.ArrayList;
import java.util.Collection;
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
    @MockBean
    private StudentService studentService;
    @SpyBean
    private FacultyService facultyService;

    @InjectMocks
    private FacultyController facultyController;

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
        List<Faculty> faculties = new ArrayList<>();
        faculties.add(new Faculty("wer", "red"));
        faculties.add(new Faculty("sad", "red"));

        String color = "red";
        when(facultyService.getAllFacultyColor(anyString())).thenReturn(faculties);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty")
                        .param("color", color))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(faculties.size())))
                .andExpect(jsonPath("$[0].name").value("wer"))
                .andExpect(jsonPath("$[0].color").value("red"))
                .andExpect(jsonPath("$[1].name").value("sad"))
                .andExpect(jsonPath("$[1].color").value("red"));
        verify(facultyService).getAllFacultyColor(color);
    }

    @Test
    public void testGetStudentsByFaculty() throws Exception {
        Student student0 = new Student("bob", 22);
        Student student1 = new Student("jon", 23);

        student0.setFaculty(new Faculty());
        student1.setFaculty(new Faculty());

        Collection<Student> students = new ArrayList<>();
        students.add(student0);
        students.add(student1);

        Long id = 1L;
        when(studentRepository.findByFacultyId(anyLong())).thenReturn(students);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/students-by-faculty-id")
                        .param("id", String.valueOf(id)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(students.size())))
                .andExpect(jsonPath("$[0].faculty").value(student0.getFaculty()))
                .andExpect(jsonPath("$[1].faculty").value(student1.getFaculty()));
    }
    @Test
    public void testGetFacultyByNameColor() throws Exception {
        List<Faculty> faculties = new ArrayList<>();
        faculties.add(new Faculty("123", "qwe"));
        faculties.add(new Faculty("var", "js"));

        String color= "red";
        String name = "wer";
        when(facultyRepository.findFacultyByColorIgnoreCase(anyString())).thenReturn(faculties);
        when(facultyRepository.findFacultyByNameIgnoreCase(anyString())).thenReturn(faculties);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/find-name-by-color")
                        .param("name", name)
                        .param("color", color))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(faculties.size() * 2)));
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
