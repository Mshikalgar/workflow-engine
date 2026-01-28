package com.mansur.workflow.workflow_engine.controller;
import org.springframework.security.core.Authentication;
import com.mansur.workflow.workflow_engine.controller.RequestController;

import com.mansur.workflow.workflow_engine.service.ApprovalEngineService;
import com.mansur.workflow.workflow_engine.service.RequestService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.mansur.workflow.workflow_engine.entity.enums.RequestType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/requests")
public class RequestController {

    private final RequestService requestService;
    private final ApprovalEngineService approvalEngineService;

    public RequestController(RequestService requestService,
                             ApprovalEngineService approvalEngineService) {
        this.requestService = requestService;
        this.approvalEngineService = approvalEngineService;
    }
    
    // STEP 1: Create a new request
    @PostMapping
    public ResponseEntity<?> createRequest(
            @RequestParam RequestType type,
            Authentication authentication) {

        String username = authentication.getName();

        return ResponseEntity.ok(
                requestService.createRequest(type, username)
        );
    }

    // STEP 2: Get request by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getRequestById(@PathVariable Long id) {

        return requestService.getRequestById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // STEP 3: Approve request
    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveRequest(
            @PathVariable Long id,
            Authentication authentication) {

        String username = authentication.getName();
        String role = authentication.getAuthorities()
                .iterator()
                .next()
                .getAuthority()
                .replace("ROLE_", "");

        approvalEngineService.approve(
                id,
                username,
                com.mansur.workflow.workflow_engine.entity.enums.Role.valueOf(role)
        );

        return ResponseEntity.ok("Request approved successfully");
    }


    // STEP 4: Reject request
    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectRequest(
            @PathVariable Long id,
            Authentication authentication) {

        String username = authentication.getName();
        approvalEngineService.reject(id, username);

        return ResponseEntity.ok("Request rejected successfully");
    }


 // STEP 6: Admin override
    @PostMapping("/{id}/override")
    public ResponseEntity<?> adminOverride(
            @PathVariable Long id,
            Authentication authentication) {

        String adminUser = authentication.getName();
        approvalEngineService.adminOverride(id, adminUser);

        return ResponseEntity.ok("Request approved by admin override");
    }

    @GetMapping("/history/{id}")
    public ResponseEntity<?> getRequestHistory(@PathVariable Long id) {
        return ResponseEntity.ok(
                approvalEngineService.getHistory(id)
        );
    }


}
