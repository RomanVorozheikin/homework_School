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
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.StudentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
public class StudentControllerMVCTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentRepository studentRepository;

    @SpyBean
    private StudentService studentService;

    @InjectMocks
    private StudentController studentController;

    Student STUDENT_1 = new Student("Gordon", 24, 1L);
    Student STUDENT_2 = new Student("Bob", 25, 2L);

    Faculty FACULTY_1 = new Faculty("Non", "Red", 1L);

    @Test
    public void postGetStudentTest() throws Exception {
        Long id = 1L;
        String name = "user";
        int age = 22;

        JSONObject studentObject = new JSONObject();
        studentObject.put("id", id);
        studentObject.put("name", name);
        studentObject.put("age", age);

        Student student = new Student();
        student.setId(id);
        student.setName(name);
        student.setAge(age);

        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(studentRepository.findById(any(Long.class))).thenReturn(Optional.of(student));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/student")
                        .content(studentObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.age").value(age));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.age").value(age));
    }

    @Test
    public void testDeleteStudent() throws Exception {
        Long id = 1L;
        mockMvc.perform(delete("/student/{id}", id))
                .andExpect(status().isOk());

        verify(studentRepository,times(1)).deleteById(id);
    }

    @Test
    public void testGetAllStudentAge() throws Exception {

        when(studentService.getAllStudentAge(anyInt())).thenReturn(List.of(STUDENT_1));

        int age = 24;
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student")
                        .param("age",String.valueOf(age)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Gordon")))
                .andExpect(jsonPath("$[0].age", is(24)));

        verify(studentService).getAllStudentAge(age);
    }
    @Test
    public  void testGetBetweenAge() throws Exception {
        int min = 22;
        int max = 25;

        when(studentRepository.findByAgeBetween(min, max)).thenReturn(List.of(STUDENT_1,STUDENT_2));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/minMax")
                        .param("min", String.valueOf(min))
                        .param("max", String.valueOf(max)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)));

        verify(studentRepository, times(1)).findByAgeBetween(min, max);
    }

    @Test
    public void testGetFacultyByStudentId() throws Exception {
        STUDENT_1.setFaculty(FACULTY_1);

        when(studentRepository.findStudentById(anyLong())).thenReturn(Optional.of(STUDENT_1));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/faculty-by-student-id")
                        .param("id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(FACULTY_1.getName()))
                .andExpect(jsonPath("$.color").value(FACULTY_1.getColor()));
    }
}
