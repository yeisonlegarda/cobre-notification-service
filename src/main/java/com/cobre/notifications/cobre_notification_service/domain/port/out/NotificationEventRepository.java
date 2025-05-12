package com.cobre.notifications.cobre_notification_service.domain.port.out;

import com.cobre.notifications.cobre_notification_service.domain.model.DeliveryStatus;
import com.cobre.notifications.cobre_notification_service.domain.model.NotificationEvent;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface NotificationEventRepository {
    List<NotificationEvent> findByClientAndFilters(Instant from, Instant to, DeliveryStatus status);
    Optional<NotificationEvent> findById(String id);
    List<NotificationEvent> findByStatus(DeliveryStatus status);
    void save(NotificationEvent event);
}
