package com.uap.proiv.jobs.service.impl;

import com.uap.proiv.jobs.client.JobApiRepository;
import com.uap.proiv.jobs.dto.Job;
import com.uap.proiv.jobs.dto.JobRequest;
import com.uap.proiv.jobs.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class JobServiceImpl implements JobService {

    private static final int DEFAULT_NEW_JOB_ID = 99;

    private final JobApiRepository repository;

    @Autowired
    public JobServiceImpl(JobApiRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Job> getAllJobs() {
        return repository.getAllJobs();
    }

    @Override
    public Job getJobById(int id) {
        return repository.getAllJobs().stream()
                .filter(item -> item.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No se encontro el puesto con id: " + id));
    }

    @Override
    public Job add(JobRequest request) {
        return new Job(
                request.getName(),
                request.getSalary(),
                request.getHours(),
                DEFAULT_NEW_JOB_ID,
                request.getResources()
        );
    }
}
