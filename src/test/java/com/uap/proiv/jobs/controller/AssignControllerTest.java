package com.uap.proiv.jobs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uap.proiv.jobs.dto.AssignRequest;
import com.uap.proiv.jobs.dto.Job;
import com.uap.proiv.jobs.dto.User;
import com.uap.proiv.jobs.dto.UserJobAssigned;
import com.uap.proiv.jobs.service.UserJobAssignedService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AssignControllerTest {

    @Mock
    private UserJobAssignedService assignmentService;

    @InjectMocks
    private AssignController controller;

    private MockMvc mvc;
    private ObjectMapper mapper;
    private List<User> candidateList;

    @BeforeEach
    void init() {
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
        mapper = new ObjectMapper();

        candidateList = new ArrayList<>();
        User first = new User();
        first.setId(1);
        first.setEmail("ejemplo@as.com");
        first.setAvatar("null");
        first.setFirstName("juan");
        first.setLastName("Garcia");
        candidateList.add(first);

        User second = new User();
        second.setId(2);
        second.setEmail("ejemplo2@as.com");
        second.setAvatar("null");
        second.setFirstName("diane");
        second.setLastName("perez");
        candidateList.add(second);
    }

    @Test
    @DisplayName("POST /api/assign - Devuelve las asignaciones generadas para el cliente")
    void postAssign_returnsAssignments() throws Exception {
        AssignRequest payload = new AssignRequest();
        payload.setRequestNumber(123);
        payload.setClientName("Name");

        Job devRole = new Job();
        devRole.setId(1);
        devRole.setName("Developer");
        devRole.setSalary(5000);
        devRole.setHours(2000);
        devRole.setResources(3);

        Job designRole = new Job();
        designRole.setId(2);
        designRole.setName("Designer");
        designRole.setSalary(4500);
        designRole.setHours(1500);
        designRole.setResources(1);

        List<UserJobAssigned> mockAssignments = new ArrayList<>();
        mockAssignments.add(new UserJobAssigned(candidateList, devRole));
        mockAssignments.add(new UserJobAssigned(List.of(candidateList.getFirst()), designRole));

        when(assignmentService.assign()).thenReturn(mockAssignments);

        mvc.perform(post("/api/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Assign").isNotEmpty())
                .andExpect(jsonPath("$.Assign[0].job.name").value("Developer"))
                .andExpect(jsonPath("$.Assign[1].job.name").value("Designer"))
                .andExpect(jsonPath("$.Assign[0].users[0].first_name").value("juan"))
                .andExpect(jsonPath("$.Request_Number").value(123))
                .andExpect(jsonPath("$.Client").value("Name"));
    }
}
