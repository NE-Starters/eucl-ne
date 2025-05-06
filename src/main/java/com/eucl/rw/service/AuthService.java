package com.eucl.rw.service;


import java.util.UUID;


public interface AuthService {
    String login(String email, String password);
    UUID getCurrentUserId();
    boolean isAuthenticated();
    boolean hasRole(String roleName);
    String refreshToken(String oldToken);
}
