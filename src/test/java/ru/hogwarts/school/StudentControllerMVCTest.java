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
        List<Student> students = new ArrayList<>();
        students.add(new Student("Bob", 23));
        students.add(new Student("Jon", 23));

        when(studentService.getAllStudentAge(anyInt())).thenReturn(students);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student")
                        .param("age", "23"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Bob")))
                .andExpect(jsonPath("$[0].age", is(23)))
                .andExpect(jsonPath("$[1].name", is("Jon")))
                .andExpect(jsonPath("$[1].age", is(23)));

        verify(studentService).getAllStudentAge(23);
    }
    @Test
    public  void testGetBetweenAge() throws Exception {
        List<Student> students = new ArrayList<>();
        students.add(new Student("Bob", 24));
        students.add(new Student("Jon", 23));

        int min = 22;
        int max = 25;


        when(studentRepository.findByAgeBetween(min, max)).thenReturn(students);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/minMax")
                        .param("min", String.valueOf(min))
                        .param("max", String.valueOf(max)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(students.size())));

        verify(studentRepository, times(1)).findByAgeBetween(min, max);
    }

    @Test
    public void testGetFacultyByStudentId() throws Exception {
        Faculty faculty = new Faculty();
        Student student = new Student();
        student.setFaculty(faculty);

        when(studentRepository.findStudentById(anyLong())).thenReturn(Optional.of(student));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/faculty-by-student-id")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(faculty.getId()));
    }
}
