package com.resolvex.service;

import java.util.List;

import com.resolvex.dto.response.NotificationResponse;

public interface NotificationService {

    List<NotificationResponse> getMyNotifications();

    long getUnreadCount();

    NotificationResponse markAsRead(Long notificationId);

    void createNotification(Long userId, String message);
}