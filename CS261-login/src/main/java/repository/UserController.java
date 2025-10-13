package repository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import LoginReq.LoginRequest;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") 
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        var user = userRepository.findByEmailAndCitizenId(
                request.getEmail(),
                request.getNationalId()
        );
        if (user.isPresent()) {
            return "Login success";
        } else {
            return "Invalid credentials";
        }
    }
}

