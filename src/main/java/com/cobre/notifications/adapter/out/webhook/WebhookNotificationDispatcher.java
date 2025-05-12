package com.cobre.notifications.adapter.out.webhook;

import com.cobre.notifications.cobre_notification_service.domain.model.NotificationEvent;
import com.cobre.notifications.cobre_notification_service.domain.port.out.NotificationDispatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WebhookNotificationDispatcher implements NotificationDispatcher {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${webhook.base-url}")
    private String webhookBaseUrl;

    @Override
    public boolean dispatch(NotificationEvent event) {
        try {
            String targetUrl = webhookBaseUrl + event.getId();
            ResponseEntity<String> response = restTemplate.postForEntity(
                    targetUrl,
                    event.getPayload(),
                    String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }
}
