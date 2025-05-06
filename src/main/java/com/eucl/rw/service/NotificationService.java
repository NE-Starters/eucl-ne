package com.eucl.rw.service;

import com.eucl.rw.model.Notification;

import java.util.List;
import java.util.UUID;


public interface NotificationService {
    Notification createNotification(UUID userId, String meterNumber, String message);
    List<Notification> getUserNotifications(UUID userId);
    List<Notification> getMeterNotifications(String meterNumber);
    void sendExpirationNotifications();
    void markAsEmailed(UUID notificationId);
    List<Notification> getPendingEmailNotifications();
    void processExpirationWarnings();
}
