package spring.ru.springtest.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import spring.ru.springtest.client.BookMetadataResilientClient;
import spring.ru.springtest.client.metadata.dto.BookMetadataResponse;
import spring.ru.springtest.dto.create.AuthorCreateRequest;
import spring.ru.springtest.dto.create.BookCreateRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthorControllerTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private BookMetadataResilientClient bookMetadataResilientClient;

    @Test
    void createAuthor_withValidData_shouldReturn201WithEnrichedBook() throws Exception {
        AuthorCreateRequest request = new AuthorCreateRequest()
                .name("George Orwell")
                .books(List.of(new BookCreateRequest()
                        .title("1984")
                        .publisher("Secker & Warburg")
                        .price(BigDecimal.valueOf(12.99))));

        when(bookMetadataResilientClient.createWithResilience(any(UUID.class), eq("Secker & Warburg"), eq(BigDecimal.valueOf(12.99))))
                .thenReturn(new BookMetadataResponse()
                        .publisher("Secker & Warburg")
                        .price(BigDecimal.valueOf(12.99)));

        mockMvc.perform(post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("George Orwell"));
    }

    @Test
    void createAuthor_withBlankName_shouldReturn400() throws Exception {
        AuthorCreateRequest request = new AuthorCreateRequest().name("");

        mockMvc.perform(post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAuthorById_whenExists_shouldReturn200() throws Exception {
        AuthorCreateRequest createRequest = new AuthorCreateRequest()
                .name("Jane Austen")
                .books(List.of(new BookCreateRequest()
                    .title("Some Book")
                    .publisher("Some Publisher")
                    .price(BigDecimal.TEN)));

        when(bookMetadataResilientClient.createWithResilience(any(), any(), any()))
                .thenReturn(new BookMetadataResponse().publisher("Some Publisher").price(BigDecimal.TEN));

        String response = mockMvc.perform(post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(createRequest)))
                .andReturn().getResponse().getContentAsString();

        String authorId = jsonMapper.readTree(response).get("id").asText();

        mockMvc.perform(get("/authors/{id}", authorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane Austen"));
    }

    @Test
    void getAuthorById_whenNotExists_shouldReturn404() throws Exception {
        mockMvc.perform(get("/authors/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteAuthor_thenGet_shouldReturn404() throws Exception {
        AuthorCreateRequest createRequest = new AuthorCreateRequest()
                .name("To Delete")
                .books(List.of(new BookCreateRequest()
                    .title("Some Book")
                    .publisher("Some Publisher")
                    .price(BigDecimal.TEN)));

        when(bookMetadataResilientClient.createWithResilience(any(), any(), any()))
                .thenReturn(new BookMetadataResponse().publisher("Some Publisher").price(BigDecimal.TEN));

        String response = mockMvc.perform(post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(createRequest)))
                .andReturn().getResponse().getContentAsString();
        String authorId = jsonMapper.readTree(response).get("id").asText();

        mockMvc.perform(delete("/authors/{id}", authorId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/authors/{id}", authorId))
                .andExpect(status().isNotFound());
    }
}