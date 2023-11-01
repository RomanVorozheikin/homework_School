package ru.hogwarts.school.controller;

import org.assertj.core.api.Assertions;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FacultyControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private FacultyController facultyController;
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void contextLoads() throws Exception {
        Assertions
                .assertThat(facultyController).isNotNull();
    }

    @Test
    public void testGetFaculty() throws Exception {
        Assertions
                .assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/faculty/1", String.class))
                .isNotNull();
    }

    @Test
    public void testPostFaculty() throws Exception {
        Faculty faculty = new Faculty();
        faculty.setName("123");
        faculty.setColor("qwe");

        Assertions
                .assertThat(this.restTemplate.postForObject("http://localhost:" + port + "/faculty", faculty, String.class))
                .isNotNull();
    }

    @Test
    public void testPutStudent() throws Exception {
        Faculty faculty = new Faculty();
        faculty.setName("123");
        faculty.setColor("qwe");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Faculty> httpEntity = new HttpEntity<>(faculty, headers);

        ResponseEntity<Faculty> response = restTemplate.exchange("http://localhost:" + port + "/faculty",
                HttpMethod.PUT,
                httpEntity,
                Faculty.class);

        assertEquals(200, response.getStatusCodeValue());

        Faculty updateFaculty = response.getBody();
        assertNotNull(updateFaculty);
        assertEquals("123", updateFaculty.getName());
        assertEquals("qwe", updateFaculty.getColor());
    }

    @Test
    public void testDeleteFaculty() throws Exception {
        Long facultyId = 5L;
        ResponseEntity<Void> response = restTemplate.exchange("http://localhost:" + port + "/faculty/" + facultyId,
                HttpMethod.DELETE,
                null,
                Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testGetAllFacultyColor() throws Exception {
        String color = "Зеленый";

        HttpEntity<Void> httpEntity = new HttpEntity<>(null);

        ResponseEntity<Faculty[]> response = restTemplate.exchange("http://localhost:" + port + "/faculty?color=" + color,
                HttpMethod.GET,
                httpEntity,
                Faculty[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Faculty[] faculties = response.getBody();
        assertNotNull(faculties);
    }

    @Test
    public void testFindFacultyNameOrColor() {
        String name = "Слизерин";
        String color = "Зеленый";

        // Создание HttpEntity
        HttpEntity<Void> httpEntity = new HttpEntity<>(null);

        // Отправка GET-запроса с параметрами
        ResponseEntity<Faculty[]> response = restTemplate.exchange("http://localhost:" + port + "/faculty/find-name-by-color?name=" + name + "&color=" + color,
                HttpMethod.GET,
                httpEntity,
                Faculty[].class);

        // Проверка статуса ответа
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testGetStudentByFacultyId() {
        Long facultyId = 1L;

        // Построение URL с параметром запроса
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/faculty/students-by-faculty-id")
                .queryParam("id", facultyId);

        // Отправка GET-запроса с параметром
        ResponseEntity<Student[]> response = restTemplate.exchange(builder.toUriString(),
                HttpMethod.GET,
                null,
                Student[].class);

        // Проверка статуса ответа
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}