package com.uap.proiv.jobs.controller;

import com.uap.proiv.jobs.dto.User;
import com.uap.proiv.jobs.dto.UserApiResponse;
import com.uap.proiv.jobs.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService service;

    @InjectMocks
    private UserController controller;

    private MockMvc mvc;
    private UserApiResponse apiResponse;
    private List<User> userList;

    @BeforeEach
    void init() {
        mvc = MockMvcBuilders.standaloneSetup(controller).build();

        userList = new ArrayList<>();
        User first = new User();
        first.setId(1);
        first.setEmail("ejemplo@as.com");
        first.setAvatar("null");
        first.setFirstName("juan");
        first.setLastName("Garcia");
        userList.add(first);

        User second = new User();
        second.setId(2);
        second.setEmail("ejemplo2@as.com");
        second.setAvatar("null");
        second.setFirstName("diane");
        second.setLastName("perez");
        userList.add(second);

        apiResponse = new UserApiResponse();
        apiResponse.setPage(1);
        apiResponse.setPerPage(2);
        apiResponse.setTotal(2);
        apiResponse.setTotalPages(1);
        apiResponse.setData(userList);
    }

    @Test
    @DisplayName("GET /api/user/{page} retorna listado paginado de usuarios")
    void getUsers_returnsPaginatedUsers() throws Exception {
        when(service.search(1))
                .thenReturn(apiResponse)
                .thenThrow(new RuntimeException("MSG"))
                .thenReturn(apiResponse);

        mvc.perform(get("/api/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.total").value(2));

        mvc.perform(get("/api/user/1")).andExpect(status().is5xxServerError());
        mvc.perform(get("/api/user/1")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/user/{page} captura error interno del servicio")
    void getUsers_handlesServiceException() throws Exception {
        when(service.search(2)).thenThrow(new RuntimeException("Service Error"));

        mvc.perform(get("/api/user/2"))
                .andExpect(status().is5xxServerError())
                .andExpect(content().string("Service Error"));
    }

    @Test
    @DisplayName("GET /api/user/{page} refleja numero de pagina actualizado")
    void getUsers_returnsCustomPage() throws Exception {
        apiResponse.setPage(3);
        when(service.search(1)).thenReturn(apiResponse);

        mvc.perform(get("/api/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.page").value(3))
                .andExpect(jsonPath("$.total").value(2));
    }
}
