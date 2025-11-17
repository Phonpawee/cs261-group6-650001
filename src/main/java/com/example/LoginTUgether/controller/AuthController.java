package com.example.LoginTUgether.controller;

import com.example.LoginTUgether.model.User;
import com.example.LoginTUgether.repo.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ============================
    // 1) TU LOGIN API
    // ============================
    @PostMapping("/tu-login")
    public ResponseEntity<?> loginTU(@RequestBody Map<String, String> loginData) {

        String url = "https://restapi.tu.ac.th/api/v1/auth/Ad/verify";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // *** ใช้ Application-Key จริงของคุณเอง ***
        headers.set("Application-Key", "TUb02a9103eb177455bb555a0785a0c6dfd728b36275f3dd974651a8c3b5c15686a1e26a676a446614542efd4e575909e2");

        Map<String, String> body = new HashMap<>();
        body.put("UserName", loginData.get("username"));
        body.put("PassWord", loginData.get("password"));

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response =
                    restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            String responseBody = response.getBody();
            JsonNode tuJson = objectMapper.readTree(responseBody);

            boolean status = tuJson.path("status").asBoolean(false);
            if (!status) {
                // login ไม่ผ่าน → ส่ง JSON จาก TU ตรง ๆ กลับไป
                return ResponseEntity.ok(tuJson);
            }

            // -----------------------------
            // สร้าง / อัปเดต User ในระบบ
            // -----------------------------
            String studentId = tuJson.path("username").asText(null);
            if (studentId == null || studentId.isEmpty()) {
                studentId = loginData.get("username");
            }

            String nameTh = tuJson.path("displayname_th").asText(null);
            String nameEn = tuJson.path("displayname_en").asText(null);
            String email = tuJson.path("email").asText(null);
            String faculty = tuJson.path("faculty").asText(null);
            String department = tuJson.path("department").asText(null);

            // ใช้ tu_status ถ้ามี ไม่งั้นลอง statusid
            String statusText = null;
            if (tuJson.hasNonNull("tu_status")) {
                statusText = tuJson.get("tu_status").asText();
            } else if (tuJson.hasNonNull("statusid")) {
                statusText = tuJson.get("statusid").asText();
            }

            Optional<User> userOpt = userRepository.findByStudentId(studentId);
            User user;
            if (userOpt.isPresent()) {
                user = userOpt.get();
            } else {
                user = new User();
                user.setStudentId(studentId);
                user.setRole("STUDENT"); // default ทุกคนเป็น student ก่อน
            }

            // อัปเดตข้อมูลโปรไฟล์จาก TU
            if (nameTh != null) user.setNameTh(nameTh);
            if (nameEn != null) user.setNameEn(nameEn);
            if (email != null) user.setEmail(email);
            if (faculty != null) user.setFaculty(faculty);
            if (department != null) user.setDepartment(department);
            if (statusText != null) user.setStatus(statusText);

            user = userRepository.save(user);

            // รวม JSON: TU data + app user
            ObjectNode result = (ObjectNode) tuJson;
            ObjectNode userNode = objectMapper.createObjectNode();
            userNode.put("id", user.getId());
            userNode.put("studentId", user.getStudentId());
            userNode.put("nameTh", user.getNameTh());
            userNode.put("nameEn", user.getNameEn());
            userNode.put("email", user.getEmail());
            userNode.put("faculty", user.getFaculty());
            userNode.put("department", user.getDepartment());
            userNode.put("status", user.getStatus());
            userNode.put("role", user.getRole());

            result.set("user", userNode);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("❌ Error calling TU Login API: " + e.getMessage());
        }
    }

    // ============================
    // 2) TU PROFILE API (proxy)
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
