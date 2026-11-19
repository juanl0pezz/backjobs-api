package com.uap.proiv.jobs.controller;

import com.uap.proiv.jobs.dto.Job;
import com.uap.proiv.jobs.service.JobService;
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
import java.util.NoSuchElementException;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class JobControllerTest {

    @Mock
    private JobService service;

    @InjectMocks
    private JobController controller;

    private MockMvc mvc;
    private List<Job> jobCatalog;

    @BeforeEach
    void init() {
        mvc = MockMvcBuilders.standaloneSetup(controller).build();

        jobCatalog = new ArrayList<>();

        Job devRole = new Job();
        devRole.setId(1);
        devRole.setName("Developer");
        devRole.setSalary(5000);
        devRole.setHours(2000);
        devRole.setResources(3);
        jobCatalog.add(devRole);

        Job designRole = new Job();
        designRole.setId(2);
        designRole.setName("Designer");
        designRole.setSalary(4500);
        designRole.setHours(1500);
        designRole.setResources(1);
        jobCatalog.add(designRole);
    }

    @Test
    @DisplayName("GET /api/job/all devuelve listado completo de puestos")
    void getAllJobs_returnsList() throws Exception {
        when(service.getAllJobs()).thenReturn(jobCatalog);

        mvc.perform(get("/api/job/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Developer"))
                .andExpect(jsonPath("$[1].name").value("Designer"));

        verify(service, times(1)).getAllJobs();
    }

    @Test
    @DisplayName("GET /api/job/{id} devuelve el puesto buscado")
    void getJobById_returnsSingleJob() throws Exception {
        when(service.getJobById(1)).thenReturn(jobCatalog.getFirst());

        mvc.perform(get("/api/job/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Developer"));

        verify(service, times(1)).getJobById(1);
    }

    @Test
    @DisplayName("GET /api/job/{id} maneja excepcion cuando no existe el puesto")
    void getJobById_handlesException() throws Exception {
        when(service.getJobById(999)).thenThrow(new NoSuchElementException("Job not found"));

        mvc.perform(get("/api/job/999"))
                .andExpect(status().is5xxServerError())
                .andExpect(content().string("Job not found"));
    }
}
