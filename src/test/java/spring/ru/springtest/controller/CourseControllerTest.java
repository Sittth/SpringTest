package spring.ru.springtest.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import spring.ru.springtest.dto.create.CourseCreateRequest;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CourseControllerTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @Test
    void createCourse_withValidData_shouldReturn201() throws Exception {
        CourseCreateRequest request = new CourseCreateRequest()
                .title("Java Architecture");

        mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Java Architecture"));
    }

    @Test
    void createCourse_withBlankTitle_shouldReturn400() throws Exception {
        CourseCreateRequest request = new CourseCreateRequest().title("");

        mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCourseById_whenExists_shouldReturn200() throws Exception {
        CourseCreateRequest request = new CourseCreateRequest().title("Spring Boot Deep Dive");
        String response = mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();

        String courseId = jsonMapper.readTree(response).get("id").asText();

        mockMvc.perform(get("/courses/{id}", courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Spring Boot Deep Dive"));
    }

    @Test
    void getCourseById_whenNotFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/courses/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}
