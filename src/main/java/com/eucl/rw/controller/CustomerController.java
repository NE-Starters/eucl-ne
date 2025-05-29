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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customer")
@PreAuthorize("hasRole('ROLE_CUSTOMER')")
@Tag(name = "Customer", description = "Endpoints for customer operations")
@SecurityRequirement(name = "bearerAuth")
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

    @GetMapping("/profile")
    @Operation(summary = "Get user profile", description = "Retrieves the authenticated user's profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully", content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<User> getProfile() {
        UUID userId = getCurrentUserId();
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update user profile", description = "Updates the authenticated user's profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully", content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content)
    })
    public ResponseEntity<User> updateProfile(@RequestBody User userDetails) {
        UUID userId = getCurrentUserId();
        return ResponseEntity.ok(userService.updateUser(userId, userDetails));
    }

    @GetMapping("/meters")
    @Operation(summary = "Get user's meters", description = "Retrieves all meters associated with the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Meters retrieved successfully", content = @Content(schema = @Schema(implementation = Meter.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content)
    })
    public ResponseEntity<List<Meter>> getMyMeters() {
        UUID userId = getCurrentUserId();
        return ResponseEntity.ok(meterService.getMetersByUser(userId));
    }

    @PostMapping("/tokens/purchase")
    @Operation(summary = "Purchase a token", description = "Purchases a token for a specific meter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token purchased successfully", content = @Content(schema = @Schema(implementation = Token.class))),
            @ApiResponse(responseCode = "400", description = "Invalid meter number or amount", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Meter does not belong to user", content = @Content)
    })
    public ResponseEntity<Token> purchaseToken(@RequestParam String meterNumber,
            @RequestParam double amount) {
        UUID userId = getCurrentUserId();
        if (meterService.getMetersByUser(userId).stream()
                .noneMatch(m -> m.getMeterNumber().equals(meterNumber))) {
            throw new RuntimeException("Meter does not belong to user");
        }
        return ResponseEntity.ok(tokenService.generateToken(meterNumber, amount));
    }

    @GetMapping("/tokens")
    @Operation(summary = "Get user's tokens", description = "Retrieves all tokens purchased by the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tokens retrieved successfully", content = @Content(schema = @Schema(implementation = Token.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content)
    })
    public ResponseEntity<List<Token>> getMyTokens() {
        UUID userId = getCurrentUserId();
        return ResponseEntity.ok(tokenService.getUserTokens(userId));
    }

    @GetMapping("/tokens/{meterNumber}")
    @Operation(summary = "Get tokens by meter", description = "Retrieves all tokens for a specific meter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tokens retrieved successfully", content = @Content(schema = @Schema(implementation = Token.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Meter does not belong to user", content = @Content)
    })
    public ResponseEntity<List<Token>> getMeterTokens(@PathVariable String meterNumber) {
        UUID userId = getCurrentUserId();
        if (meterService.getMetersByUser(userId).stream()
                .noneMatch(m -> m.getMeterNumber().equals(meterNumber))) {
            throw new RuntimeException("Meter does not belong to user");
        }
        return ResponseEntity.ok(tokenService.getTokensByMeter(meterNumber));
    }

    @GetMapping("/tokens/validate/{token}")
    @Operation(summary = "Validate a token", description = "Validates a token's authenticity and status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token validated successfully", content = @Content(schema = @Schema(implementation = Token.class))),
            @ApiResponse(responseCode = "400", description = "Invalid token", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content)
    })
    public ResponseEntity<Token> validateToken(@PathVariable String token) {
        Token validatedToken = tokenService.validateToken(token);
        return ResponseEntity.ok(validatedToken);
    }

    @GetMapping("/notifications")
    @Operation(summary = "Get user's notifications", description = "Retrieves all notifications for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully", content = @Content(schema = @Schema(implementation = Notification.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content)
    })
    public ResponseEntity<List<Notification>> getMyNotifications() {
        UUID userId = getCurrentUserId();
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }

    private UUID getCurrentUserId() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getId();
        }
        throw new RuntimeException("Unauthenticated");
    }
}