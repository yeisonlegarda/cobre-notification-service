package com.cobre.notifications.cobre_notification_service.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DeliveryStatus {
    @JsonProperty("pending")
    PENDING,

    @JsonProperty("completed")
    COMPLETED,

    @JsonProperty("failed")
    FAILED
}
