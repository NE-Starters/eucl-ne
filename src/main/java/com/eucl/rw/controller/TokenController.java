package com.eucl.rw.controller;

import com.eucl.rw.enums.ETokenStatus;
import com.eucl.rw.model.Token;
import com.eucl.rw.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/tokens")
@Tag(name = "Token", description = "Public endpoints for token information and status")
public class TokenController {

    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @GetMapping("/{token}")
    @Operation(summary = "Get token information", description = "Retrieves detailed information about a specific token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token information retrieved successfully", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "Invalid token", content = @Content),
            @ApiResponse(responseCode = "404", description = "Token not found", content = @Content)
    })
    public ResponseEntity<?> getTokenInfo(@PathVariable String token) {
        Token tokenInfo = tokenService.validateToken(token);
        Map<String, Object> response = new HashMap<>();
        response.put("token", tokenInfo.getToken());
        response.put("formattedToken", tokenInfo.getFormattedToken());
        response.put("days", tokenInfo.getTokenValueDays());
        response.put("status", tokenInfo.getTokenStatus());
        response.put("purchasedDate", tokenInfo.getPurchasedDate());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{token}/status")
    @Operation(summary = "Get token status", description = "Retrieves the status of a specific token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token status retrieved successfully", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "Invalid token", content = @Content),
            @ApiResponse(responseCode = "404", description = "Token not found", content = @Content)
    })
    public ResponseEntity<?> getTokenStatus(@PathVariable String token) {
        ETokenStatus status = tokenService.checkTokenStatus(token);
        return ResponseEntity.ok(Collections.singletonMap("status", status));
    }
}