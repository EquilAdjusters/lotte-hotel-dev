package com.example.backendlotte.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backendlotte.notification.entity.NotificationLog;

public interface NotificationLogRepository
        extends JpaRepository<NotificationLog, Long> {
}