package com.eucl.rw.repository;

import com.eucl.rw.model.Notification;
import com.eucl.rw.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByUser(User user);
    List<Notification> findByMeterNumber(String meterNumber);

    @Query("SELECT n FROM Notification n WHERE n.issuedDate >= :startDate AND n.issuedDate <= :endDate")
    List<Notification> findNotificationsBetweenDates(LocalDateTime startDate, LocalDateTime endDate);

    List<Notification> findByEmailed(boolean emailed);

    List<Notification> findByUser_Id(UUID userId);
}
