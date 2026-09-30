package com.resolvex.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.resolvex.entity.Notification;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByUser_UserIdOrderByCreatedAtDesc(
            Long userId);

    Optional<Notification> findByNotificationIdAndUser_UserId(
            Long notificationId,
            Long userId);

    long countByUser_UserIdAndIsReadFalse(Long userId);
}