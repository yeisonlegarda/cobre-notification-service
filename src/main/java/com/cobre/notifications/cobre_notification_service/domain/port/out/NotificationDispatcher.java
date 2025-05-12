package com.cobre.notifications.cobre_notification_service.domain.port.out;

import com.cobre.notifications.cobre_notification_service.domain.model.NotificationEvent;

public interface NotificationDispatcher {
    boolean dispatch(NotificationEvent event);
}
