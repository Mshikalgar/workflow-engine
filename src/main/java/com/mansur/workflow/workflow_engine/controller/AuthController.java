package com.mansur.workflow.workflow_engine.controller;

import com.mansur.workflow.workflow_engine.security.jwt.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/login")
    public Map<String, String> login(
            @RequestParam String username,
            @RequestParam String role) {

        String token = JwtUtil.generateToken(username, role);
        return Map.of("token", token);
    }
}
