package com.riverlake.controller;

import com.riverlake.dto.LoginResponse;
import com.riverlake.dto.LoginRequest;
import com.riverlake.dto.SendCodeRequest;
import com.riverlake.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/send-code")
    public ResponseEntity<Map<String, String>> sendCode(@Valid @RequestBody SendCodeRequest request) {
        authService.sendCode(request.getPhone());
        
        Map<String, String> result = new HashMap<>();
        result.put("message", "验证码已发送");
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request.getPhone(), request.getCode());
        return ResponseEntity.ok(response);
    }
}
