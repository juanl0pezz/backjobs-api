package com.uap.proiv.jobs.service.impl;

import com.uap.proiv.jobs.dto.AssignedResponse;
import com.uap.proiv.jobs.dto.Job;
import com.uap.proiv.jobs.dto.User;
import com.uap.proiv.jobs.dto.UserApiResponse;
import com.uap.proiv.jobs.dto.UserJobAssigned;
import com.uap.proiv.jobs.service.AssignedService;
import com.uap.proiv.jobs.service.JobService;
import com.uap.proiv.jobs.service.UserJobAssignedService;
import com.uap.proiv.jobs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserJobAssignedServiceImpl implements UserJobAssignedService {

    private final JobService jobService;
    private final UserService userService;
    private final AssignedService assignedService;

    @Autowired
    public UserJobAssignedServiceImpl(JobService jobService,
                                      UserService userService,
                                      AssignedService assignedService) {
        this.jobService = jobService;
        this.userService = userService;
        this.assignedService = assignedService;
    }

    @Override
    public List<UserJobAssigned> assign() {
        List<Job> availableJobs = jobService.getAllJobs();
        List<User> allUsers = fetchAllPaginatedUsers();

        List<Integer> userIds = allUsers.stream()
                .map(User::getId)
                .toList();

        List<AssignedResponse> rawAssignments = assignedService.create(availableJobs, userIds);

        Map<Integer, User> usersById = allUsers.stream()
                .collect(Collectors.toMap(User::getId, Function.identity(), (existing, duplicate) -> existing));

        List<UserJobAssigned> result = new ArrayList<>();
        for (Job job : availableJobs) {
            List<User> assignedUsers = rawAssignments.stream()
                    .filter(item -> item.jobId() == job.getId())
                    .map(item -> {
                        User match = usersById.get(item.userId());
                        if (match == null) {
                            throw new IllegalStateException("Usuario no encontrado para el id: " + item.userId());
                        }
                        return match;
                    })
                    .toList();
            result.add(new UserJobAssigned(assignedUsers, job));
        }
        return result;
    }

    private List<User> fetchAllPaginatedUsers() {
        UserApiResponse currentResponse = userService.search(1);
        List<User> collectedUsers = new ArrayList<>(currentResponse.getData());
        int maxPages = currentResponse.getTotalPages();

        while (currentResponse != null && currentResponse.getPage() <= maxPages) {
            int nextPage = currentResponse.getPage() + 1;
            currentResponse = userService.search(nextPage);
            if (currentResponse != null && currentResponse.getData() != null) {
                collectedUsers.addAll(currentResponse.getData());
            }
        }
        return collectedUsers;
    }
}
