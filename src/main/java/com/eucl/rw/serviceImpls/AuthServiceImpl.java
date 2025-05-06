package com.eucl.rw.serviceImpls;

import com.eucl.rw.model.User;
import com.eucl.rw.service.AuthService;
import com.eucl.rw.service.UserService;
import com.eucl.rw.util.JwtTokenUtil;
import com.eucl.rw.util.UserPrincipal;
import io.jsonwebtoken.*;
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

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);


    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Override
    public String login(String email, String password) {
        User user = userService.authenticateUser(email, password); // Ensure authentication checks credentials
        return jwtTokenUtil.generateToken(user); // Returns JWT token
    }

    @Override
    public UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            return userPrincipal.getId();
        }
        throw new RuntimeException("Unauthenticated");
    }

    @Override
    public boolean isAuthenticated() {
        return SecurityContextHolder.getContext().getAuthentication() != null;
    }

    @Override
    public boolean hasRole(String roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities() != null) {
            return authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals(roleName));
        }
        return false;
    }

    @Override
    public String refreshToken(String oldToken) {
        if (validateToken(oldToken)) {
            String email = jwtTokenUtil.getUserNameFromJwtToken(oldToken);
            User user = userService.getUserByEmail(email); // Fetch user using the email
            return jwtTokenUtil.generateToken(user); // Generate new token based on user info
        }
        throw new RuntimeException("Invalid or expired token");
    }

    private String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userService.loadUserByUsername(getEmailFromToken(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(jwtSecret)
                    .build()
                    .parseClaimsJws(authToken); // Throws exception if invalid
            return true;
        } catch (JwtException ex) {
            logger.error("JWT validation failed: {}", ex);
        }
        return false;
    }
}
