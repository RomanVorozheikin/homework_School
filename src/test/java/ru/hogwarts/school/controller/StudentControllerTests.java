package ru.hogwarts.school.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerTests {

	@LocalServerPort
	private int port;

	@Autowired
	private StudentController studentController;
	@Autowired
	private TestRestTemplate restTemplate;


	@Test
	public void contextLoads() throws Exception {
		Assertions
				.assertThat(studentController).isNotNull();
	}

	@Test
	public void testGetStudent() throws Exception {
		Assertions
				.assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/student/1", String.class))
				.isNotNull();
	}

	@Test
	public void testPostStudent() throws Exception {
		Student student = new Student();
		student.setName("Ivan");
		student.setAge(33);

		Assertions
				.assertThat(this.restTemplate.postForObject("http://localhost:" + port + "/student", student, String.class))
				.isNotNull();
	}

	@Test
	public void testPutStudent() throws Exception {
		Student student = new Student();
		student.setName("Ivan");
		student.setAge(44);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Student> httpEntity = new HttpEntity<>(student, headers);

		ResponseEntity<Student> response = restTemplate.exchange("http://localhost:" + port + "/student",
				HttpMethod.PUT,
				httpEntity,
				Student.class);

		assertEquals(200, response.getStatusCodeValue());

		Student updateStudent = response.getBody();
		assertNotNull(updateStudent);
		assertEquals("Ivan", updateStudent.getName());
		assertEquals(44, updateStudent.getAge());
	}

	@Test
	public void testDeleteStudent() throws Exception {
		Long studentId = 5L;
		ResponseEntity<Void> response = restTemplate.exchange("http://localhost:" + port + "/student/" + studentId,
				HttpMethod.DELETE,
				null,
				Void.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void testGetAllStudentAge() throws Exception {
		int age = 22;

		HttpEntity<Void> httpEntity = new HttpEntity<>(null);

		// Отправка GET-запроса с параметрами
		ResponseEntity<Student[]> response = restTemplate.exchange("http://localhost:" + port + "/student?age=" + age,
				HttpMethod.GET,
				httpEntity,
				Student[].class);

		// Проверка статуса ответа
		assertEquals(HttpStatus.OK, response.getStatusCode());

		// Проверка полученных данных
		Student[] students = response.getBody();
		assertNotNull(students);
	}

	@Test
	public void testGetStudentBetweenMinMax() {
		int ageMin = 20;
		int ageMax = 26;

		HttpEntity<Void> httpEntity = new HttpEntity<>(null);

		ResponseEntity<Student[]> response = restTemplate.exchange("http://localhost:" + port + "/student/minMax?min=" + ageMin + "&max=" + ageMax,
				HttpMethod.GET,
				httpEntity,
				Student[].class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		Student[] students = response.getBody();
		assertNotNull(students);
	}

	@Test
	public void testGetFacultyByStudentId() {
		Long studentId = 1L;

		// Построение URL с параметром запроса
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/student/faculty-by-student-id")
				.queryParam("id", studentId);

		// Отправка GET-запроса с параметром
		ResponseEntity<Faculty> response = restTemplate.exchange(builder.toUriString(),
				HttpMethod.GET,
				null,
				Faculty.class);

		// Проверка статуса ответа
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
}
