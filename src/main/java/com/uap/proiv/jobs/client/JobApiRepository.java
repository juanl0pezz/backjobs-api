package com.uap.proiv.jobs.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uap.proiv.jobs.dto.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class JobApiRepository {

    private static final String JOBS_RESOURCE_PATH = "jobs.json";

    private final ObjectMapper mapper;
    private final List<Job> cachedJobs;

    @Autowired
    public JobApiRepository(ObjectMapper mapper) {
        this.mapper = mapper;
        this.cachedJobs = loadJobsFromFile();
    }

    public List<Job> getAllJobs() {
        return cachedJobs;
    }

    private List<Job> loadJobsFromFile() {
        ClassPathResource resource = new ClassPathResource(JOBS_RESOURCE_PATH);
        try (InputStream stream = resource.getInputStream()) {
            return mapper.readValue(stream, new TypeReference<>() {});
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo cargar el archivo " + JOBS_RESOURCE_PATH, ex);
        }
    }
}
