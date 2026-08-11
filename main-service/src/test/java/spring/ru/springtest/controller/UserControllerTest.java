package spring.ru.springtest.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import spring.ru.springtest.dto.create.ProfileCreateRequest;
import spring.ru.springtest.dto.create.UserCreateRequest;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @Test
    void createUser_withValidData_shouldReturn201() throws Exception {
        UserCreateRequest request = new UserCreateRequest()
                .username("john_doe")
                .profile(new ProfileCreateRequest().bio("Software Engineer"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value("john_doe"))
                .andExpect(jsonPath("$.profile.bio").value("Software Engineer"));
    }

    @Test
    void getUserById_whenNotFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/users/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}
