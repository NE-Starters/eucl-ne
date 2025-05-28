package com.eucl.rw.service;

import com.eucl.rw.response.AuthResponse;
import java.util.UUID;

public interface AuthService {
    AuthResponse login(String email, String password);

    UUID getCurrentUserId();

    boolean isAuthenticated();

    boolean hasRole(String roleName);

    AuthResponse refreshToken(String oldToken);

    void logout(String accessToken);
}