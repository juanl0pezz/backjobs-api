package com.uap.proiv.jobs.service.impl;

import com.uap.proiv.jobs.dto.AssignedResponse;
import com.uap.proiv.jobs.dto.Job;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Tests unitarios de AssignedServiceImpl")
class AssignedServiceImplTest {

    private final AssignedServiceImpl service = new AssignedServiceImpl();

    private Job buildJob(int id, int resources) {
        Job item = new Job();
        item.setId(id);
        item.setResources(resources);
        return item;
    }

    @Test
    @DisplayName("Debe generar tantas asignaciones como recursos requiera el puesto")
    void create_generatesExpectedAssignmentCount() {
        Job devJob = buildJob(10, 3);
        List<Integer> candidates = new ArrayList<>(List.of(1, 2, 3, 4, 5));

        List<AssignedResponse> assignments = service.create(List.of(devJob), candidates);

        assertThat(assignments).hasSize(3);
        assertThat(assignments).allMatch(item -> item.jobId() == 10);
    }

    @Test
    @DisplayName("Debe asignar unicamente IDs presentes en la lista de usuarios")
    void create_usesOnlyProvidedUserIds() {
        Job devJob = buildJob(10, 2);
        List<Integer> candidates = new ArrayList<>(List.of(100, 200, 300));

        List<AssignedResponse> assignments = service.create(List.of(devJob), candidates);

        assertThat(assignments)
                .extracting(AssignedResponse::userId)
                .allMatch(candidates::contains);
    }

    @Test
    @DisplayName("Debe acumular asignaciones cuando se reciben multiples puestos")
    void create_aggregatesMultipleJobs() {
        Job firstJob = buildJob(1, 2);
        Job secondJob = buildJob(2, 1);
        List<Integer> candidates = new ArrayList<>(List.of(1, 2, 3));

        List<AssignedResponse> assignments = service.create(List.of(firstJob, secondJob), candidates);

        assertThat(assignments).hasSize(3);
        assertThat(assignments).filteredOn(a -> a.jobId() == 1).hasSize(2);
        assertThat(assignments).filteredOn(a -> a.jobId() == 2).hasSize(1);
    }

    @Test
    @DisplayName("Debe retornar lista vacia si no se envian puestos")
    void create_returnsEmptyWhenNoJobsProvided() {
        List<AssignedResponse> assignments = service.create(List.of(), new ArrayList<>(List.of(1, 2)));

        assertThat(assignments).isEmpty();
    }

    @Test
    @DisplayName("Debe lanzar IndexOutOfBoundsException si los recursos pedidos superan los usuarios disponibles")
    void create_throwsExceptionWhenUsersAreInsufficient() {
        Job demandingJob = buildJob(1, 5);
        List<Integer> candidates = new ArrayList<>(List.of(1, 2));

        assertThatThrownBy(() -> service.create(List.of(demandingJob), candidates))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }
}
