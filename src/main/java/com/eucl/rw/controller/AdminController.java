package com.eucl.rw.controller;

import com.eucl.rw.enums.ERole;
import com.eucl.rw.enums.ETokenStatus;
import com.eucl.rw.model.*;
import com.eucl.rw.service.MeterService;
import com.eucl.rw.service.TokenService;
import com.eucl.rw.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {
    private final UserService userService;
    private final MeterService meterService;
    private final TokenService tokenService;

    public AdminController(UserService userService, MeterService meterService,
            TokenService tokenService) {
        this.userService = userService;
        this.meterService = meterService;
        this.tokenService = tokenService;
    }

    // User Management
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user,
            @RequestParam Set<ERole> roles) {
        return ResponseEntity.ok(userService.registerUser(user, roles));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable UUID userId,
            @RequestBody User userDetails) {
        return ResponseEntity.ok(userService.updateUser(userId, userDetails));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    // Meter Management
    @PostMapping("/meters")
    public ResponseEntity<Meter> registerMeter(@RequestParam UUID userId) {
        return ResponseEntity.ok(meterService.registerMeter(userId));
    }

    @GetMapping("/meters")
    public ResponseEntity<List<Meter>> getAllMeters() {
        return ResponseEntity.ok(meterService.getAllMeters());
    }

    @GetMapping("/users/{userId}/meters")
    public ResponseEntity<List<Meter>> getUserMeters(@PathVariable UUID userId) {
        return ResponseEntity.ok(meterService.getMetersByUser(userId));
    }

    @PutMapping("/meters/{meterNumber}/transfer")
    public ResponseEntity<?> transferMeter(@PathVariable String meterNumber,
            @RequestParam UUID newUserId) {
        meterService.transferMeter(meterNumber, newUserId);
        return ResponseEntity.ok().build();
    }

    // Token Management
    @GetMapping("/tokens")
    public ResponseEntity<List<Token>> getAllTokens() {
        return ResponseEntity.ok(tokenService.getTokensByStatus(null));
    }

    @GetMapping("/tokens/status/{status}")
    public ResponseEntity<List<Token>> getTokensByStatus(@PathVariable ETokenStatus status) {
        return ResponseEntity.ok(tokenService.getTokensByStatus(status));
    }

    @GetMapping("/users/{userId}/tokens")
    public ResponseEntity<List<Token>> getUserTokens(@PathVariable UUID userId) {
        return ResponseEntity.ok(tokenService.getUserTokens(userId));
    }
}
