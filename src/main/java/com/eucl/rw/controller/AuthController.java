package com.eucl.rw.controller;

import com.eucl.rw.dto.LoginDTO;
import com.eucl.rw.dto.UserRegistrationDTO;
import com.eucl.rw.enums.ERole;
import com.eucl.rw.model.User;
import com.eucl.rw.service.AuthService;
import com.eucl.rw.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDTO registrationDTO) {
        User user = new User();
        user.setName(registrationDTO.getName());
        user.setEmail(registrationDTO.getEmail());
        user.setPhone(registrationDTO.getPhone());
        user.setNationalId(registrationDTO.getNationalId());
        user.setPassword(registrationDTO.getPassword());

        Set<ERole> roles = new HashSet<>();
        roles.add(ERole.ROLE_CUSTOMER); // Assign default role

        User registeredUser = userService.registerUser(user, roles);
        return ResponseEntity.ok(Map.of(
                "message", "User registered successfully",
                "user", registeredUser
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginDTO loginDTO) {
        // Assuming login() returns a JWT or throws an exception if credentials are invalid
        String token = authService.login(loginDTO.getEmail(), loginDTO.getPassword());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Invalid Authorization header");
        }

        String token = authHeader.substring(7);
        String newToken = authService.refreshToken(token);

        return ResponseEntity.ok(Collections.singletonMap("token", newToken));
    }
}
