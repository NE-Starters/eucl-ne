package com.eucl.rw.controller;

import com.eucl.rw.model.Meter;
import com.eucl.rw.model.Notification;
import com.eucl.rw.model.Token;
import com.eucl.rw.model.User;
import com.eucl.rw.service.MeterService;
import com.eucl.rw.service.NotificationService;
import com.eucl.rw.service.TokenService;
import com.eucl.rw.service.UserService;
import com.eucl.rw.util.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customer")
@PreAuthorize("hasRole('ROLE_CUSTOMER')")
public class CustomerController {

    private final UserService userService;
    private final MeterService meterService;
    private final TokenService tokenService;
    private final NotificationService notificationService;

    public CustomerController(UserService userService, MeterService meterService,
                              TokenService tokenService, NotificationService notificationService) {
        this.userService = userService;
        this.meterService = meterService;
        this.tokenService = tokenService;
        this.notificationService = notificationService;
    }

    // User Profile
    @GetMapping("/profile")
    public ResponseEntity<User> getProfile() {
        UUID userId = getCurrentUserId();
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(@RequestBody User userDetails) {
        UUID userId = getCurrentUserId();
        return ResponseEntity.ok(userService.updateUser(userId, userDetails));
    }

    // Meter Operations
    @GetMapping("/meters")
    public ResponseEntity<List<Meter>> getMyMeters() {
        UUID userId = getCurrentUserId();
        return ResponseEntity.ok(meterService.getMetersByUser(userId));
    }

    // Token Operations
    @PostMapping("/tokens/purchase")
    public ResponseEntity<Token> purchaseToken(@RequestParam String meterNumber,
                                               @RequestParam double amount) {
        UUID userId = getCurrentUserId();
        // Verify the meter belongs to the user
        if (meterService.getMetersByUser(userId).stream()
                .noneMatch(m -> m.getMeterNumber().equals(meterNumber))) {
            throw new RuntimeException("Meter does not belong to user");
        }

        return ResponseEntity.ok(tokenService.generateToken(meterNumber, amount));
    }

    @GetMapping("/tokens")
    public ResponseEntity<List<Token>> getMyTokens() {
        UUID userId = getCurrentUserId();
        return ResponseEntity.ok(tokenService.getUserTokens(userId));
    }

    @GetMapping("/tokens/{meterNumber}")
    public ResponseEntity<List<Token>> getMeterTokens(@PathVariable String meterNumber) {
        UUID userId = getCurrentUserId();
        // Verify the meter belongs to the user
        if (meterService.getMetersByUser(userId).stream()
                .noneMatch(m -> m.getMeterNumber().equals(meterNumber))) {
            throw new RuntimeException("Meter does not belong to user");
        }

        return ResponseEntity.ok(tokenService.getTokensByMeter(meterNumber));
    }

    @GetMapping("/tokens/validate/{token}")
    public ResponseEntity<Token> validateToken(@PathVariable String token) {
        Token validatedToken = tokenService.validateToken(token);
        return ResponseEntity.ok(validatedToken);
    }

    // Notifications
    @GetMapping("/notifications")
    public ResponseEntity<List<Notification>> getMyNotifications() {
        UUID userId = getCurrentUserId();
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }


    public UUID getCurrentUserId() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getId();
        }
        throw new RuntimeException("Unauthenticated");
    }
}