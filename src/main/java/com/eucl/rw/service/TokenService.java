package com.eucl.rw.service;

import com.eucl.rw.enums.ETokenStatus;
import com.eucl.rw.model.Token;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


public interface TokenService {
    Token generateToken(String meterNumber, double amount);
    Token validateToken(String token);
    List<Token> getTokensByMeter(String meterNumber);
    List<Token> getUserTokens(UUID userId);
    List<Token> getTokensByStatus(ETokenStatus status);
    String formatTokenDisplay(String token);
    List<Token> checkExpiringTokens(LocalDateTime expirationThreshold);
    void expireTokens(LocalDateTime expirationDate);
    ETokenStatus checkTokenStatus(String token);
    double calculateTokenDays(double amount);
}
