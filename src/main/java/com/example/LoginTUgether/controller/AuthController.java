package com.example.LoginTUgether.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // ============================
    // 1) TU LOGIN API
    // ============================
	@PostMapping("/tu-login")
	public ResponseEntity<?> loginTU(@RequestBody Map<String, String> loginData) {

	    String url = "https://restapi.tu.ac.th/api/v1/auth/Ad/verify";
	    RestTemplate restTemplate = new RestTemplate();

	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    headers.set("Application-Key", "TUb02a9103eb177455bb555a0785a0c6dfd728b36275f3dd974651a8c3b5c15686a1e26a676a446614542efd4e575909e2"); // << ต้องมีแบบเพื่อน

	    Map<String, String> body = new HashMap<>();
	    body.put("UserName", loginData.get("username")); // ต้องเป็นชื่อนี้!
	    body.put("PassWord", loginData.get("password"));

	    HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

	    try {
	        ResponseEntity<String> response =
	                restTemplate.exchange(url, HttpMethod.POST, request, String.class);

	        return ResponseEntity.ok(response.getBody());

	    } catch (Exception e) {
	        return ResponseEntity.status(500)
	                .body("❌ Error calling TU Login API: " + e.getMessage());
	    }
	}



    // ============================
    // 2) TU PROFILE API
    // ============================
	@GetMapping("/tu-profile")
	public ResponseEntity<?> getProfile(@RequestParam String stdId) {

	    String url = "https://restapi.tu.ac.th/api/v2/profile/std/info/?id=" + stdId;

	    RestTemplate restTemplate = new RestTemplate();

	    HttpHeaders headers = new HttpHeaders();
	    headers.set("Application-Key", "TUb02a9103eb177455bb555a0785a0c6dfd728b36275f3dd974651a8c3b5c15686a1e26a676a446614542efd4e575909e2");

	    HttpEntity<Void> request = new HttpEntity<>(headers);

	    try {
	        ResponseEntity<String> response =
	                restTemplate.exchange(url, HttpMethod.GET, request, String.class);

	        return ResponseEntity.ok(response.getBody());

	    } catch (Exception e) {
	        return ResponseEntity.status(500)
	                .body("❌ Error calling TU Profile API: " + e.getMessage());
	    }
	}
}
