package com.example.LoginTUgether.controller;

import org.springframework.http.*;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.HashMap;


@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/std-info")
    public ResponseEntity<?> getProfile(@RequestParam("id") String stdId) {

        String url = "https://restapi.tu.ac.th/api/v2/profile/std/info/?id=" + stdId;

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Application-Key", "TUb02a9103eb177455bb555a0785a0c6dfd728b36275f3dd974651a8c3b5c15686a1e26a676a446614542efd4e575909e2");

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response =
                    restTemplate.exchange(url, HttpMethod.GET, request, String.class);

            // แปลง JSON String → JSON Object
            Object json = objectMapper.readValue(response.getBody(), Object.class);

            return ResponseEntity.ok(json);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}