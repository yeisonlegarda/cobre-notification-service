package com.cobre.notifications.application;

import com.cobre.notifications.cobre_notification_service.domain.model.DeliveryStatus;
import com.cobre.notifications.cobre_notification_service.domain.model.NotificationEvent;
import com.cobre.notifications.cobre_notification_service.domain.port.out.NotificationDispatcher;
import com.cobre.notifications.cobre_notification_service.domain.port.out.NotificationEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Mock
    private NotificationEventRepository repository;

    @Mock
    private NotificationDispatcher dispatcher;

    @InjectMocks
    private NotificationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldListEvents() {
        Instant from = Instant.now().minusSeconds(3600);
        Instant to = Instant.now();
        DeliveryStatus status = DeliveryStatus.FAILED;

        when(repository.findByClientAndFilters(from, to, status))
                .thenReturn(List.of(new NotificationEvent()));

        List<NotificationEvent> result = service.listEvents(from, to, status);
        assertEquals(1, result.size());
        verify(repository).findByClientAndFilters(from, to, status);
    }

    @Test
    void shouldGetEventById() {
        NotificationEvent event = new NotificationEvent();
        event.setId("evt123");
        when(repository.findById("evt123")).thenReturn(Optional.of(event));

        Optional<NotificationEvent> found = service.getEvent("evt123");

        assertTrue(found.isPresent());
        assertEquals("evt123", found.get().getId());
    }

    @Test
    void shouldReplayFailedEventSuccessfully() {
        NotificationEvent failedEvent = new NotificationEvent();
        failedEvent.setId("evt123");
        failedEvent.setDeliveryStatus(DeliveryStatus.FAILED);

        when(repository.findById("evt123")).thenReturn(Optional.of(failedEvent));
        when(dispatcher.dispatch(failedEvent)).thenReturn(true);

        boolean result = service.replayEvent("evt123");

        assertTrue(result);
        assertEquals(DeliveryStatus.COMPLETED, failedEvent.getDeliveryStatus());
        verify(repository).save(failedEvent);
    }

    @Test
    void shouldNotReplayNonFailedEvent() {
        NotificationEvent deliveredEvent = new NotificationEvent();
        deliveredEvent.setId("evt123");
        deliveredEvent.setDeliveryStatus(DeliveryStatus.COMPLETED);

        when(repository.findById("evt123")).thenReturn(Optional.of(deliveredEvent));

        boolean result = service.replayEvent("evt123");

        assertFalse(result);
        verify(dispatcher, never()).dispatch(any());
        verify(repository, never()).save(any());
    }

    @Test
    void shouldReplayAllFailedEvents() {
        NotificationEvent failed1 = new NotificationEvent();
        failed1.setId("evt1");
        failed1.setDeliveryStatus(DeliveryStatus.FAILED);

        NotificationEvent failed2 = new NotificationEvent();
        failed2.setId("evt2");
        failed2.setDeliveryStatus(DeliveryStatus.FAILED);

        when(repository.findByStatus(DeliveryStatus.FAILED))
                .thenReturn(List.of(failed1, failed2));
        when(dispatcher.dispatch(any())).thenReturn(true);

        int count = service.replayAllEvents();

        assertEquals(2, count);
        verify(repository, times(2)).save(any());
    }
}