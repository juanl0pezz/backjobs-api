package com.uap.proiv.jobs.service.impl;

import com.uap.proiv.jobs.dto.AssignedResponse;
import com.uap.proiv.jobs.dto.Job;
import com.uap.proiv.jobs.dto.User;
import com.uap.proiv.jobs.dto.UserApiResponse;
import com.uap.proiv.jobs.dto.UserJobAssigned;
import com.uap.proiv.jobs.service.AssignedService;
import com.uap.proiv.jobs.service.JobService;
import com.uap.proiv.jobs.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests con @Spy en UserJobAssignedServiceImpl")
public class UserJobAssignedServiceImplSpyTest {

    @Mock
    private JobService jobService;

    @Mock
    private UserService userService;

    @Spy
    private AssignedService assignedService;

    @InjectMocks
    private UserJobAssignedServiceImpl orchestratorService;

    private List<Job> jobList;
    private List<User> userList;
    private List<AssignedResponse> stubbedAssignments;
    private UserApiResponse pageResponse;

    @BeforeEach
    void init() {
        jobList = new ArrayList<>();

        Job devRole = new Job();
        devRole.setId(1);
        devRole.setName("Developer");
        devRole.setSalary(5000);
        devRole.setHours(2000);
        devRole.setResources(3);
        jobList.add(devRole);

        Job designRole = new Job();
        designRole.setId(2);
        designRole.setName("Designer");
        designRole.setSalary(4500);
        designRole.setHours(1500);
        designRole.setResources(1);
        jobList.add(designRole);

        userList = new ArrayList<>();

        User firstCandidate = new User();
        firstCandidate.setId(1);
        firstCandidate.setEmail("ejemplo@as.com");
        firstCandidate.setAvatar(null);
        firstCandidate.setFirstName("juan");
        firstCandidate.setLastName("Garcia");
        userList.add(firstCandidate);

        User secondCandidate = new User();
        secondCandidate.setId(2);
        secondCandidate.setEmail("ejemplo2@as.com");
        secondCandidate.setAvatar(null);
        secondCandidate.setFirstName("diane");
        secondCandidate.setLastName("perez");
        userList.add(secondCandidate);

        pageResponse = new UserApiResponse();
        pageResponse.setPage(1);
        pageResponse.setPerPage(2);
        pageResponse.setTotal(2);
        pageResponse.setTotalPages(1);
        pageResponse.setData(userList);

        stubbedAssignments = new ArrayList<>();
        stubbedAssignments.add(new AssignedResponse(1, 1));
        stubbedAssignments.add(new AssignedResponse(2, 2));
    }

    @Test
    @DisplayName("assign() ejecuta correctamente utilizando @Spy sobre AssignedService")
    void assign_singlePageWithSpy() {
        when(jobService.getAllJobs()).thenReturn(jobList);
        when(userService.search(1)).thenReturn(pageResponse);

        doReturn(stubbedAssignments).when(assignedService).create(any(), any());

        List<UserJobAssigned> output = orchestratorService.assign();

        assertNotNull(output);
        assertEquals(2, output.size());
        assertEquals(1, output.get(0).getUsers().getFirst().getId());
        assertEquals(2, output.get(1).getUsers().getFirst().getId());

        verify(jobService, times(1)).getAllJobs();
        verify(userService, times(1)).search(1);
        verify(assignedService, times(1)).create(jobList, List.of(1, 2));
    }
}
