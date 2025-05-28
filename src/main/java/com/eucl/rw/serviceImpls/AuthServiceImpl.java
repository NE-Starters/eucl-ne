package com.eucl.rw.serviceImpls;

import com.eucl.rw.model.RefreshToken;
import com.eucl.rw.model.User;
import com.eucl.rw.service.AuthService;
import com.eucl.rw.service.RefreshTokenService;
import com.eucl.rw.service.UserService;
import com.eucl.rw.util.JwtTokenUtil;
import com.eucl.rw.util.UserPrincipal;
import com.eucl.rw.response.AuthResponse;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Value("${app.jwt.access-token-expiration-ms:900000}") // 15 minutes
    private int accessTokenExpirationMs;

    @Override
    public AuthResponse login(String email, String password) {
        try {
            User user = userService.authenticateUser(email, password);
            String accessToken = jwtTokenUtil.generateAccessToken(user);
            String refreshToken = refreshTokenService.createRefreshToken(user).getToken();
            logger.info("User {} logged in successfully", email);
            return new AuthResponse(accessToken, refreshToken);
        } catch (Exception e) {
            logger.error("Login failed for user {}: {}", email, e.getMessage());
            throw new SecurityException("Invalid credentials", e);
        }
    }

    @Override
    public UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getId();
        }
        logger.warn("Attempt to access user ID without authentication");
        throw new SecurityException("Unauthenticated");
    }

    @Override
    public boolean isAuthenticated() {
        return SecurityContextHolder.getContext().getAuthentication() != null &&
               SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
    }

    @Override
    public boolean hasRole(String roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities() != null) {
            boolean hasRole = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals(roleName));
            logger.debug("Checking role {}: {}", roleName, hasRole);
            return hasRole;
        }
        logger.warn("No authentication found for role check: {}", roleName);
        return false;
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        try {
            RefreshToken token = refreshTokenService.validateRefreshToken(refreshToken);
            User user = userService.getUserById(token.getUserId());
            String newAccessToken = jwtTokenUtil.generateAccessToken(user);
            String newRefreshToken = refreshTokenService.rotateRefreshToken(token).getToken();
            logger.info("Token refreshed for user ID: {}", user.getId());
            return new AuthResponse(newAccessToken, newRefreshToken);
        } catch (Exception e) {
            logger.error("Token refresh failed: {}", e.getMessage());
            throw new SecurityException("Invalid or expired refresh token", e);
        }
    }

    @Override
    public void logout(String accessToken) {
        if (jwtTokenUtil.validateJwtToken(accessToken)) {
            jwtTokenUtil.blacklistToken(accessToken, accessTokenExpirationMs);
            logger.info("User logged out, token blacklisted");
        } else {
            logger.warn("Attempt to logout with invalid token");
            throw new SecurityException("Invalid token");
        }
    }

    public Authentication getAuthentication(String token) {
        if (jwtTokenUtil.isTokenBlacklisted(token)) {
            logger.warn("Attempt to use blacklisted token");
            throw new SecurityException("Token is blacklisted");
        }
        String email = jwtTokenUtil.getUserNameFromJwtToken(token);
        UserDetails userDetails = userService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public boolean validateToken(String authToken) {
        try {
            if (jwtTokenUtil.isTokenBlacklisted(authToken)) {
                logger.warn("Token is blacklisted");
                return false;
            }
            return jwtTokenUtil.validateJwtToken(authToken);
        } catch (JwtException ex) {
            logger.error("JWT validation failed: {}", ex.getMessage());
            return false;
        }
    }
}
