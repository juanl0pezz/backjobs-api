package com.uap.proiv.jobs.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.uap.proiv.jobs.dto.User;
import com.uap.proiv.jobs.dto.UserApiResponse;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

@Component
public class UserApiRepository {

    private static final String DEFAULT_BASE_URL = "https://reqres.in/api/users";
    private static final String DEFAULT_API_KEY = "free_user_3HYTiqu2JKQ4TfGq884xW5mqfrd";
    private static final int TIMEOUT_SECONDS = 10;

    private final HttpClient client;
    private final ObjectMapper mapper;
    private final String usersEndpointUrl;
    private final String apiKeyHeader;

    public UserApiRepository() {
        this(
                HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                        .build(),
                new ObjectMapper(),
                DEFAULT_BASE_URL,
                DEFAULT_API_KEY
        );
    }

    public UserApiRepository(HttpClient client, ObjectMapper mapper, String usersEndpointUrl, String apiKeyHeader) {
        this.client = client;
        this.mapper = mapper;
        this.usersEndpointUrl = usersEndpointUrl;
        this.apiKeyHeader = apiKeyHeader;
    }

    public UserApiResponse getUsers(int page) {
        try {
            URI targetUri = URI.create(usersEndpointUrl + "?page=" + page);
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(targetUri)
                    .header("Accept", "application/json")
                    .header("X-API-KEY", apiKeyHeader)
                    .GET()
                    .build();

            HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (httpResponse.statusCode() != 200) {
                throw new RuntimeException("Fallo al consultar ReqRes API. Status HTTP: " + httpResponse.statusCode());
            }
            return mapper.readValue(httpResponse.body(), UserApiResponse.class);
        } catch (Exception ex) {
            throw new RuntimeException("Error de comunicacion con el servicio externo de usuarios: " + ex.getMessage(), ex);
        }
    }

    public User getUserById(int id) {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(usersEndpointUrl + "/" + id))
                    .header("Accept", "application/json")
                    .header("X-API-KEY", apiKeyHeader)
                    .GET()
                    .build();

            HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (httpResponse.statusCode() != 200) {
                throw new RuntimeException("Fallo al consultar ReqRes API. Status HTTP: " + httpResponse.statusCode());
            }

            JsonNode responseNode = mapper.readTree(httpResponse.body());
            JsonNode userNode = responseNode.has("data") ? responseNode.get("data") : responseNode;
            return mapper.treeToValue(userNode, User.class);
        } catch (Exception ex) {
            throw new RuntimeException("Error de comunicacion con el servicio externo de usuarios: " + ex.getMessage(), ex);
        }
    }

    public void updateUser(User user) {
        try {
            Map<String, String> payload = Map.of(
                    "name", user.getFirstName(),
                    "job", user.getLastName()
            );
            String jsonBody = mapper.writeValueAsString(payload);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(usersEndpointUrl + "/" + user.getId()))
                    .header("Accept", "application/json")
                    .header("X-API-KEY", apiKeyHeader)
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (httpResponse.statusCode() != 200) {
                throw new RuntimeException("Fallo al actualizar usuario en ReqRes API. Status HTTP: " + httpResponse.statusCode());
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error de comunicacion al actualizar usuario: " + ex.getMessage(), ex);
        }
    }
}
