package com.uap.proiv.jobs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uap.proiv.jobs.client.UserApiRepository;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UserApiRepository repository;

    private static MockWebServer server;

    @BeforeAll
    public static void startServer() throws IOException {
        server = new MockWebServer();
        server.start();
    }

    @AfterAll
    public static void stopServer() throws IOException {
        server.close();
    }

    @TestConfiguration
    static class IntegrationTestConfig {
        @Bean
        @Primary
        public UserApiRepository userApiRepository(ObjectMapper objectMapper) {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();
            String endpointUrl = server.url("/api/users").toString();
            String key = "free_user_3HYTiqu2JKQ4TfGq884xW5mqfrd";

            return new UserApiRepository(client, objectMapper, endpointUrl, key);
        }
    }

    @Test
    @DisplayName("GET /api/user/id/{id} prueba de integracion con MockWebServer")
    void getUserById_integration() throws Exception {
        String responsePayload = """
                {
                   "id": 2,
                   "email": "juan@gmail.com",
                   "first_name": "Juan",
                   "last_name": "Perez",
                   "avatar": "https://reqres.in/img/faces/2.jpg"
                }
                """;
        server.enqueue(new MockResponse()
                .setBody(responsePayload)
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
        );

        mvc.perform(get("/api/user/id/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.email").value("juan@gmail.com"))
                .andExpect(jsonPath("$.first_name").value("Juan"))
                .andExpect(jsonPath("$.last_name").value("Perez"))
                .andExpect(jsonPath("$.avatar").value("https://reqres.in/img/faces/2.jpg"));

        RecordedRequest recorded = server.takeRequest();
        assertEquals("application/json", recorded.getHeader("Accept"));
        assertEquals("free_user_3HYTiqu2JKQ4TfGq884xW5mqfrd", recorded.getHeader("X-API-KEY"));
    }

    @Test
    @DisplayName("POST /api/user/update prueba de integracion con MockWebServer")
    void updateUser_integration() throws Exception {
        String apiResponse = """
                {
                 "name": "Carlos",
                 "job": "Analista",
                 "updatedAt": "2024-01-01T12:00:00.000Z"
                }
                """;

        server.enqueue(new MockResponse()
                .setBody(apiResponse)
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
        );

        String requestBody = """
                {
                    "id": 1,
                    "first_name": "Carlos",
                    "last_name": "Perez"
                }
                """;

        mvc.perform(post("/api/user/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("User updated successfully"));
    }
}
