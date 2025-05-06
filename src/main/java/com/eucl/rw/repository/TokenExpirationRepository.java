package com.eucl.rw.repository;

import com.eucl.rw.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TokenExpirationRepository extends JpaRepository<Token, UUID> {

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
    int expireOldTokens(LocalDateTime expirationDate);
}