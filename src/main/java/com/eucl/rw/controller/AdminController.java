package com.eucl.rw.controller;

import com.eucl.rw.enums.ERole;
import com.eucl.rw.enums.ETokenStatus;
import com.eucl.rw.model.Meter;
import com.eucl.rw.model.Token;
import com.eucl.rw.model.User;
import com.eucl.rw.service.MeterService;
import com.eucl.rw.service.TokenService;
import com.eucl.rw.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@Tag(name = "Admin", description = "Endpoints for administrative operations")
@SecurityRequirement(name = "bearerAuth")
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

    @GetMapping("/users")
    @Operation(summary = "Get all users", description = "Retrieves a list of all registered users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully", content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required", content = @Content)
    })
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping("/users")
    @Operation(summary = "Create a new user", description = "Creates a new user with specified roles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created successfully", content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user data or roles", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required", content = @Content),
            @ApiResponse(responseCode = "409", description = "User already exists", content = @Content)
    })
    public ResponseEntity<User> createUser(@RequestBody User user,
            @RequestParam Set<ERole> roles) {
        return ResponseEntity.ok(userService.registerUser(user, roles));
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "Get user by ID", description = "Retrieves a user by their unique ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully", content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<User> getUserById(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PutMapping("/users/{userId}")
    @Operation(summary = "Update user", description = "Updates user details by their unique ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully", content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<User> updateUser(@PathVariable UUID userId,
            @RequestBody User userDetails) {
        return ResponseEntity.ok(userService.updateUser(userId, userDetails));
    }

    @DeleteMapping("/users/{userId}")
    @Operation(summary = "Delete user", description = "Deletes a user by their unique ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<?> deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/meters")
    @Operation(summary = "Register a meter", description = "Registers a new meter for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Meter registered successfully", content = @Content(schema = @Schema(implementation = Meter.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user ID", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<Meter> registerMeter(@RequestParam UUID userId) {
        return ResponseEntity.ok(meterService.registerMeter(userId));
    }

    @GetMapping("/meters")
    @Operation(summary = "Get all meters", description = "Retrieves a list of all registered meters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Meters retrieved successfully", content = @Content(schema = @Schema(implementation = Meter.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required", content = @Content)
    })
    public ResponseEntity<List<Meter>> getAllMeters() {
        return ResponseEntity.ok(meterService.getAllMeters());
    }

    @GetMapping("/users/{userId}/meters")
    @Operation(summary = "Get user's meters", description = "Retrieves all meters for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Meters retrieved successfully", content = @Content(schema = @Schema(implementation = Meter.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<List<Meter>> getUserMeters(@PathVariable UUID userId) {
        return ResponseEntity.ok(meterService.getMetersByUser(userId));
    }

    @PutMapping("/meters/{meterNumber}/transfer")
    @Operation(summary = "Transfer meter", description = "Transfers a meter to a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Meter transferred successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid meter number or user ID", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required", content = @Content),
            @ApiResponse(responseCode = "404", description = "Meter or user not found", content = @Content)
    })
    public ResponseEntity<?> transferMeter(@PathVariable String meterNumber,
            @RequestParam UUID newUserId) {
        meterService.transferMeter(meterNumber, newUserId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tokens")
    @Operation(summary = "Get all tokens", description = "Retrieves a list of all tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tokens retrieved successfully", content = @Content(schema = @Schema(implementation = Token.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required", content = @Content)
    })
    public ResponseEntity<List<Token>> getAllTokens() {
        return ResponseEntity.ok(tokenService.getTokensByStatus(null));
    }

    @GetMapping("/tokens/status/{status}")
    @Operation(summary = "Get tokens by status", description = "Retrieves tokens filtered by status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tokens retrieved successfully", content = @Content(schema = @Schema(implementation = Token.class))),
            @ApiResponse(responseCode = "400", description = "Invalid status", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required", content = @Content)
    })
    public ResponseEntity<List<Token>> getTokensByStatus(@PathVariable ETokenStatus status) {
        return ResponseEntity.ok(tokenService.getTokensByStatus(status));
    }

    @GetMapping("/users/{userId}/tokens")
    @Operation(summary = "Get user's tokens", description = "Retrieves all tokens for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tokens retrieved successfully", content = @Content(schema = @Schema(implementation = Token.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<List<Token>> getUserTokens(@PathVariable UUID userId) {
        return ResponseEntity.ok(tokenService.getUserTokens(userId));
    }
}