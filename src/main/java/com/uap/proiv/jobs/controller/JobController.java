package com.uap.proiv.jobs.controller;

import com.uap.proiv.jobs.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/job")
public class JobController {

    private final JobService service;

    @Autowired
    public JobController(JobService service) {
        this.service = service;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllJobs() {
        try {
            return ResponseEntity.ok(service.getAllJobs());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getJobById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(service.getJobById(id));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }
}
