package com.eucl.rw.serviceImpls;

import com.eucl.rw.enums.ETokenStatus;
import com.eucl.rw.model.Meter;
import com.eucl.rw.model.Token;
import com.eucl.rw.repository.MeterRepository;
import com.eucl.rw.repository.TokenRepository;
import com.eucl.rw.service.TokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@Transactional
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;
    private final MeterRepository meterRepository;
    private final Random random = new Random();

    public TokenServiceImpl(TokenRepository tokenRepository, MeterRepository meterRepository) {
        this.tokenRepository = tokenRepository;
        this.meterRepository = meterRepository;
    }

    @Override
    public Token generateToken(String meterNumber, double amount) {
        if (amount < 100) {
            throw new RuntimeException("Minimum amount is 100 RWF");
        }
        if (amount % 100 != 0) {
            throw new RuntimeException("Amount must be in multiples of 100 RWF");
        }

        int days = (int) (amount / 100);
        if (days > (365 * 5)) {
            throw new RuntimeException("Cannot purchase more than 5 years of electricity");
        }

        Meter meter = meterRepository.findByMeterNumber(meterNumber)
                .orElseThrow(() -> new RuntimeException("Meter not found"));

        String token = generateRandomToken();
        Token newToken = new Token();
        newToken.setMeter(meter);
        newToken.setUser(meter.getUser());
        newToken.setToken(token);
        newToken.setTokenStatus(ETokenStatus.NEW);
        newToken.setTokenValueDays(days);
        newToken.setPurchasedDate(LocalDateTime.now());
        newToken.setAmount(amount);

        return tokenRepository.save(newToken);
    }

    private String generateRandomToken() {
        StringBuilder token = new StringBuilder(16);
        for (int i = 0; i < 16; i++) {
            token.append(random.nextInt(10));
        }
        return token.toString();
    }

    @Override
    public Token validateToken(String token) {
        Token foundToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        if (foundToken.getTokenStatus() != ETokenStatus.NEW) {
            throw new RuntimeException("Token is " + foundToken.getTokenStatus().name().toLowerCase());
        }

        return foundToken;
    }

    @Override
    public List<Token> getTokensByMeter(String meterNumber) {
        return tokenRepository.findByMeter_MeterNumber(meterNumber);
    }

    @Override
    public List<Token> getUserTokens(UUID userId) {
        return tokenRepository.findByUser_Id(userId);
    }

    @Override
    public List<Token> getTokensByStatus(ETokenStatus status) {
        return tokenRepository.findByTokenStatus(status);
    }

    @Override
    public String formatTokenDisplay(String token) {
        return token.replaceAll("(.{4})(?=.)", "$1-");
    }

    @Override
    public List<Token> checkExpiringTokens(LocalDateTime expirationThreshold) {
        return tokenRepository.findTokensNearingExpiration(
                expirationThreshold,
                expirationThreshold.minusHours(5));
    }

    @Override
    public void expireTokens(LocalDateTime expirationDate) {
        tokenRepository.expireOldTokens(expirationDate);
    }

    @Override
    public ETokenStatus checkTokenStatus(String token) {
        return tokenRepository.findByToken(token)
                .map(Token::getTokenStatus)
                .orElseThrow(() -> new RuntimeException("Token not found"));
    }

    @Override
    public double calculateTokenDays(double amount) {
        return amount / 100;
    }
}