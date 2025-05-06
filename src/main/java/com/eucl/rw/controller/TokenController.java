package com.eucl.rw.controller;

import com.eucl.rw.enums.ETokenStatus;
import com.eucl.rw.model.Token;
import com.eucl.rw.service.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/tokens")
public class TokenController {

    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @GetMapping("/{token}")
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
    public ResponseEntity<?> getTokenStatus(@PathVariable String token) {
        ETokenStatus status = tokenService.checkTokenStatus(token);
        return ResponseEntity.ok(Collections.singletonMap("status", status));
    }
}