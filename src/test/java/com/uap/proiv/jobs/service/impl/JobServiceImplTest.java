package com.uap.proiv.jobs.service.impl;

import com.uap.proiv.jobs.client.JobApiRepository;
import com.uap.proiv.jobs.dto.Job;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios de JobServiceImpl")
class JobServiceImplTest {

    @Mock
    private JobApiRepository repository;

    private JobServiceImpl service;

    private Job backendRole;
    private Job qaRole;

    @BeforeEach
    void init() {
        service = new JobServiceImpl(repository);

        backendRole = new Job();
        backendRole.setId(1);
        backendRole.setName("Desarrollador Backend");
        backendRole.setSalary(1500.0);
        backendRole.setHours(40);
        backendRole.setResources(2);

        qaRole = new Job();
        qaRole.setId(2);
        qaRole.setName("QA Analyst");
        qaRole.setSalary(1200.0);
        qaRole.setHours(30);
        qaRole.setResources(1);
    }

    @Test
    @DisplayName("getAllJobs() retorna el listado completo del repositorio")
    void getAllJobs_returnsAllFromRepository() {
        List<Job> expected = List.of(backendRole, qaRole);
        when(repository.getAllJobs()).thenReturn(expected);

        List<Job> actual = service.getAllJobs();

        assertThat(actual).isEqualTo(expected);
        verify(repository).getAllJobs();
    }

    @Test
    @DisplayName("getJobById() encuentra y retorna el puesto correspondiente al ID")
    void getJobById_returnsMatchingJob() {
        when(repository.getAllJobs()).thenReturn(List.of(backendRole, qaRole));

        Job actual = service.getJobById(2);

        assertThat(actual).isEqualTo(qaRole);
    }

    @Test
    @DisplayName("getJobById() lanza NoSuchElementException cuando el ID no existe")
    void getJobById_throwsWhenNotFound() {
        when(repository.getAllJobs()).thenReturn(List.of(backendRole, qaRole));

        assertThatThrownBy(() -> service.getJobById(999))
                .isInstanceOf(NoSuchElementException.class);
    }
}
