package com.example.LoginTUgether.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/tu-login")
    public ResponseEntity<?> tuLogin(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        String apiKey = "TU769ca8a024d034946c9ccdb37e8d08dd94096935bf874e34086f669eb2fc3d91ce762b24dd97e397543bd99840f0ade0"; // <-- ใส่ API key จาก https://restapi.tu.ac.th/

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        Map<String, String> body = new HashMap<>();
        body.put("UserName", username);
        body.put("PassWord", password);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://restapi.tu.ac.th/api/v1/auth/Ad/verify",
                request,
                String.class
        );

        return ResponseEntity.ok(response.getBody());
    }
}
