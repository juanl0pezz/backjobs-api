package com.uap.proiv.jobs.service.impl;

import com.uap.proiv.jobs.dto.AssignedResponse;
import com.uap.proiv.jobs.dto.Job;
import com.uap.proiv.jobs.dto.User;
import com.uap.proiv.jobs.dto.UserApiResponse;
import com.uap.proiv.jobs.dto.UserJobAssigned;
import com.uap.proiv.jobs.service.AssignedService;
import com.uap.proiv.jobs.service.JobService;
import com.uap.proiv.jobs.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios de UserJobAssignedServiceImpl")
class UserJobAssignedServiceImplTest {

    @Mock
    private JobService jobService;

    @Mock
    private UserService userService;

    @Mock
    private AssignedService assignedService;

    @InjectMocks
    private UserJobAssignedServiceImpl orchestratorService;

    private Job createJob(int id, int resources) {
        Job item = new Job();
        item.setId(id);
        item.setResources(resources);
        return item;
    }

    private User createUser(int id) {
        User item = new User();
        item.setId(id);
        return item;
    }

    private UserApiResponse buildPage(int page, int totalPages, List<User> users) {
        UserApiResponse response = new UserApiResponse();
        response.setPage(page);
        response.setTotalPages(totalPages);
        response.setData(users);
        return response;
    }

    @Test
    @DisplayName("assign() agrupa los usuarios asignados dentro de cada puesto")
    void assign_mapsUsersToEachJob() {
        Job devJob = createJob(1, 1);
        User firstUser = createUser(100);

        when(jobService.getAllJobs()).thenReturn(List.of(devJob));
        when(userService.search(1)).thenReturn(buildPage(1, 1, List.of(firstUser)));
        when(assignedService.create(List.of(devJob), List.of(100)))
                .thenReturn(List.of(new AssignedResponse(1, 100)));

        List<UserJobAssigned> assignments = orchestratorService.assign();

        assertThat(assignments).hasSize(1);
        UserJobAssigned entry = assignments.getFirst();
        assertThat(entry.getJob()).isEqualTo(devJob);
        assertThat(entry.getUsers()).containsExactly(firstUser);
    }

    @Test
    @DisplayName("assign() recorre todas las paginas de usuarios disponibles antes de asignar")
    void assign_iteratesOverAllUserPages() {
        Job devJob = createJob(1, 2);
        User firstUser = createUser(100);
        User secondUser = createUser(200);

        when(jobService.getAllJobs()).thenReturn(List.of(devJob));
        when(userService.search(1)).thenReturn(buildPage(1, 2, List.of(firstUser)));
        when(userService.search(2)).thenReturn(buildPage(2, 2, List.of(secondUser)));
        when(assignedService.create(eq(List.of(devJob)), eq(List.of(100, 200))))
                .thenReturn(List.of(
                        new AssignedResponse(1, 100),
                        new AssignedResponse(1, 200)
                ));

        List<UserJobAssigned> assignments = orchestratorService.assign();

        assertThat(assignments).hasSize(1);
        assertThat(assignments.getFirst().getUsers()).containsExactlyInAnyOrder(firstUser, secondUser);

        verify(userService, times(1)).search(1);
        verify(userService, times(1)).search(2);
        verify(userService, times(1)).search(3);
    }

    @Test
    @DisplayName("assign() retorna lista vacia cuando no existen puestos")
    void assign_returnsEmptyWhenNoJobsExist() {
        when(jobService.getAllJobs()).thenReturn(List.of());
        when(userService.search(1)).thenReturn(buildPage(1, 1, List.of()));
        when(assignedService.create(List.of(), List.of())).thenReturn(List.of());

        List<UserJobAssigned> assignments = orchestratorService.assign();

        assertThat(assignments).isEmpty();
    }
}
