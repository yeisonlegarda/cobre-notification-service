package com.cobre.notifications.adapter.in.rest;

import com.cobre.notifications.adapter.in.rest.dto.EventCounterDTO;
import com.cobre.notifications.adapter.in.rest.dto.NotificationEventDTO;
import com.cobre.notifications.application.NotificationService;
import com.cobre.notifications.cobre_notification_service.domain.model.DeliveryStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notification_events")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping
    public List<NotificationEventDTO> getNotifications(@RequestParam Instant from,
                                                       @RequestParam Instant to,
                                                       @RequestParam DeliveryStatus status) {
        return service.listEvents(from, to, status).stream()
                .map(NotificationEventDTO::from)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationEventDTO> getNotification(@PathVariable String id) {
        return service.getEvent(id)
                .map(NotificationEventDTO::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/replay")
    public ResponseEntity<Void> replayNotification(@PathVariable String id) {
        return service.replayEvent(id)
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @PostMapping("/replay_all")
    public EventCounterDTO replayAllFailedNotifications() {
        return EventCounterDTO.builder().successEventCounter(service.replayAllEvents()).build();
    }
}
