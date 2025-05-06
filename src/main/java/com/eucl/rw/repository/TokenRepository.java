package com.eucl.rw.repository;


import com.eucl.rw.enums.ETokenStatus;
import com.eucl.rw.model.Token;
import com.eucl.rw.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public interface TokenRepository extends JpaRepository<Token, UUID> {
    List<Token> findByMeter_MeterNumber(String meterNumber);
    List<Token> findByUser(User user);
    List<Token> findByTokenStatus(ETokenStatus status);

    @Query("SELECT t FROM Token t WHERE t.meter.meterNumber = :meterNumber AND t.tokenStatus = 'NEW'")
    List<Token> findActiveTokensByMeter(String meterNumber);

    @Query("SELECT t FROM Token t WHERE t.purchasedDate <= :expiryDate AND t.tokenStatus = 'NEW'")
    List<Token> findTokensAboutToExpire(LocalDateTime expiryDate);

    Optional<Token> findByToken(String token);

    List<Token> findByUser_Id(UUID userId);

    @Query("SELECT t FROM Token t WHERE " +
            "t.tokenStatus = 'NEW' AND " +
            "t.purchasedDate <= :expirationThreshold AND " +
            "t.purchasedDate > :notificationThreshold")
    List<Token> findTokensNearingExpiration(
            LocalDateTime expirationThreshold,
            LocalDateTime notificationThreshold
    );


    @Modifying
    @Query("UPDATE Token t SET t.tokenStatus = 'EXPIRED' WHERE " +
            "t.tokenStatus = 'NEW' AND " +
            "t.purchasedDate <= :expirationDate")
    void expireOldTokens(LocalDateTime expirationDate);
}
