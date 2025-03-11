package com.memmcol.apigatewayservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class FallbackController {
    @GetMapping("/fallback")
    public ResponseEntity<String> fallback() {
        return ResponseEntity.ok("The service is currently unavailable. Please try again later.");
    }
}

