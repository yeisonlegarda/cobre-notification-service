package com.cobre.notifications.cobre_notification_service.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationEvent {
    @JsonProperty("event_id")
    private String id;

    @JsonProperty("event_type")
    private String eventType;

    @JsonProperty("content")
    private String payload;

    @JsonProperty("delivery_status")
    private DeliveryStatus deliveryStatus;

    @JsonProperty("delivery_date")
    private Instant deliveredAt;

    private Instant createdAt = Instant.now();;

}
