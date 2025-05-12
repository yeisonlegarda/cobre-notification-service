package com.cobre.notifications.application;


import com.cobre.notifications.cobre_notification_service.domain.model.DeliveryStatus;
import com.cobre.notifications.cobre_notification_service.domain.model.NotificationEvent;
import com.cobre.notifications.cobre_notification_service.domain.port.out.NotificationDispatcher;
import com.cobre.notifications.cobre_notification_service.domain.port.out.NotificationEventRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    private final NotificationEventRepository repository;
    private final NotificationDispatcher dispatcher;

    public NotificationService(NotificationEventRepository repository, NotificationDispatcher dispatcher) {
        this.repository = repository;
        this.dispatcher = dispatcher;
    }

    public List<NotificationEvent> listEvents(Instant from, Instant to, DeliveryStatus status) {
        return repository.findByClientAndFilters(from, to, status);
    }

    public Optional<NotificationEvent> getEvent(String id) {
        return repository.findById(id);
    }

    public boolean replayEvent(String id) {
        Optional<NotificationEvent> eventOpt = repository.findById(id);
        if (eventOpt.isPresent() && eventOpt.get().getDeliveryStatus() == DeliveryStatus.FAILED) {
            NotificationEvent event = eventOpt.get();
            boolean success = dispatcher.dispatch(event);
            event.setDeliveryStatus(success ? DeliveryStatus.COMPLETED : DeliveryStatus.FAILED);
            event.setDeliveredAt(Instant.now());
            repository.save(event);
            return success;
        }
        return false;
    }

    public int replayAllEvents(){
        List<NotificationEvent> failedEvents = repository.findByStatus(DeliveryStatus.FAILED);
        int successEvents = 0;
        for (NotificationEvent event : failedEvents) {
            boolean success = dispatcher.dispatch(event);
            event.setDeliveryStatus(success ? DeliveryStatus.COMPLETED: DeliveryStatus.FAILED);
            event.setDeliveredAt(Instant.now());
            repository.save(event);
            if(success) successEvents ++;
        }
        return successEvents;
    }
}
