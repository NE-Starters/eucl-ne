package com.eucl.rw.serviceImpls;

import com.eucl.rw.model.Notification;
import com.eucl.rw.model.Token;
import com.eucl.rw.model.User;
import com.eucl.rw.repository.NotificationRepository;
import com.eucl.rw.repository.TokenRepository;
import com.eucl.rw.repository.UserRepository;
import com.eucl.rw.service.NotificationService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JavaMailSender mailSender;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   UserRepository userRepository,
                                   TokenRepository tokenRepository,
                                   JavaMailSender mailSender) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.mailSender = mailSender;
    }

    @Override
    public Notification createNotification(UUID userId, String meterNumber, String message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMeterNumber(meterNumber);
        notification.setMessage(message);
        notification.setIssuedDate(LocalDateTime.now());
        notification.setEmailed(false);

        return notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getUserNotifications(UUID userId) {
        return notificationRepository.findByUser_Id((userId));
    }

    @Override
    public List<Notification> getMeterNotifications(String meterNumber) {
        return notificationRepository.findByMeterNumber(meterNumber);
    }

    @Override
    public void sendExpirationNotifications() {
        LocalDateTime threshold = LocalDateTime.now().plusHours(5);
        List<Token> expiringTokens = tokenRepository.findTokensNearingExpiration(
                threshold,
                threshold.minusHours(1)
        );

        for (Token token : expiringTokens) {
            String message = String.format(
                    "Dear %s, EUCL is pleased to remind you that the token in the %s is going to expire in 5 hours. Please purchase a new token.",
                    token.getUser().getName(),
                    token.getMeter().getMeterNumber()
            );

            Notification notification = createNotification(
                    token.getUser().getId(),
                    token.getMeter().getMeterNumber(),
                    message
            );

            sendEmailNotification(notification);
        }
    }

    private void sendEmailNotification(Notification notification) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(notification.getUser().getEmail());
        mailMessage.setSubject("EUCL Token Expiration Notification");
        mailMessage.setText(notification.getMessage());

        mailSender.send(mailMessage);
        notification.setEmailed(true);
        notificationRepository.save(notification);
    }

    @Override
    public void markAsEmailed(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setEmailed(true);
        notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getPendingEmailNotifications() {
        return notificationRepository.findByEmailed(false);
    }

    @Override
    public void processExpirationWarnings() {
        sendExpirationNotifications();
    }
}