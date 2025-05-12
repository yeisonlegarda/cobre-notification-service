package com.cobre.notifications.adapter.in.rest.dto;

import com.cobre.notifications.cobre_notification_service.domain.model.NotificationEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationEventDTO {
    private String id;
    private String eventType;
    private String deliveryStatus;
    private String createdAt;
    private String deliveredAt;

    public static NotificationEventDTO from(NotificationEvent event) {
        return NotificationEventDTO.builder()
                .id(event.getId())
                .eventType(event.getEventType())
                .deliveryStatus(event.getDeliveryStatus().name())
                .createdAt(event.getCreatedAt().toString())
                .deliveredAt(event.getDeliveredAt() != null ? event.getDeliveredAt().toString() : null)
                .build();
    }
}
