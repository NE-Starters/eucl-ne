package com.eucl.rw.serviceImpls;

import com.eucl.rw.model.RefreshToken;
import com.eucl.rw.model.User;
import com.eucl.rw.repository.RefreshTokenRepository;
import com.eucl.rw.service.RefreshTokenService;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenServiceImpl.class);

    @Value("${app.jwt.refresh-token-expiration-ms:604800000}") // 7 days
    private int refreshTokenExpirationMs;

    @Override
    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(user.getId());
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenExpirationMs));
        refreshTokenRepository.save(refreshToken);
        logger.info("Created refresh token for user ID: {}", user.getId());
        return refreshToken;
    }

    @Override
    public RefreshToken validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    logger.warn("Invalid refresh token: {}", token);
                    return new SecurityException("Invalid refresh token");
                });
        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            logger.warn("Expired refresh token: {}", token);
            throw new SecurityException("Refresh token expired");
        }
        return refreshToken;
    }

    @Override
    public RefreshToken rotateRefreshToken(RefreshToken oldToken) {
        refreshTokenRepository.delete(oldToken);
        RefreshToken newToken = new RefreshToken();
        newToken.setUserId(oldToken.getUserId());
        newToken.setToken(UUID.randomUUID().toString());
        newToken.setExpiryDate(Instant.now().plusMillis(refreshTokenExpirationMs));
        refreshTokenRepository.save(newToken);
        logger.info("Rotated refresh token for user ID: {}", oldToken.getUserId());
        return newToken;
    }

    @Override
    public void deleteRefreshToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(refreshToken -> {
            refreshTokenRepository.delete(refreshToken);
            logger.info("Deleted refresh token: {}", token);
        });
    }
}