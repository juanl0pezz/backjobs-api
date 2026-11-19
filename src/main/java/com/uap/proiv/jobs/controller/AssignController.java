package com.uap.proiv.jobs.controller;

import com.uap.proiv.jobs.dto.AssignRequest;
import com.uap.proiv.jobs.dto.UserJobAssigned;
import com.uap.proiv.jobs.service.UserJobAssignedService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AssignController {

    private final UserJobAssignedService assignmentService;

    @Autowired
    public AssignController(UserJobAssignedService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping("/assign")
    public ResponseEntity<Object> assign(@RequestBody @Valid AssignRequest request) {
        try {
            List<UserJobAssigned> assignments = assignmentService.assign();
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("Client", request.getClientName());
            payload.put("Request_Number", request.getRequestNumber());
            payload.put("Assign", assignments);
            return ResponseEntity.ok(payload);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }
}
