package com.riverlake.controller;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class HelloController {

    @GetMapping("/hello")
    public Map<String, Object> hello() {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Hello from RiverLake Help Backend!");
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }

    @PostMapping("/echo")
    public Map<String, Object> echo(@RequestBody Map<String, Object> data) {
        Map<String, Object> result = new HashMap<>();
        result.put("echo", data);
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }
}
