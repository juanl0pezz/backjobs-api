package com.uap.proiv.jobs.service.impl;

import com.uap.proiv.jobs.dto.AssignedResponse;
import com.uap.proiv.jobs.dto.Job;
import com.uap.proiv.jobs.service.AssignedService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class AssignedServiceImpl implements AssignedService {

    @Override
    public List<AssignedResponse> create(List<Job> jobs, List<Integer> availableUserIds) {
        List<AssignedResponse> assignments = new ArrayList<>();
        List<Integer> shuffledIds = new ArrayList<>(availableUserIds);

        for (Job currentJob : jobs) {
            Collections.shuffle(shuffledIds);
            int requiredResources = currentJob.getResources();
            for (int index = 0; index < requiredResources; index++) {
                Integer selectedUserId = shuffledIds.get(index);
                assignments.add(new AssignedResponse(currentJob.getId(), selectedUserId));
            }
        }
        return assignments;
    }
}
