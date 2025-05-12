package com.cobre.notifications.adapter.out.file;

import com.cobre.notifications.cobre_notification_service.domain.model.DeliveryStatus;
import com.cobre.notifications.cobre_notification_service.domain.model.NotificationEvent;
import com.cobre.notifications.cobre_notification_service.domain.port.out.NotificationEventRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class JsonFileNotificationEventRepository implements NotificationEventRepository {

    private final Path filePath = Paths.get("data/notification_events.json");
    private final ObjectMapper objectMapper = new ObjectMapper();

    private List<NotificationEvent> loadAll() {
        try {
            if (!Files.exists(filePath)) return new ArrayList<>();
            return objectMapper.readValue(Files.readAllBytes(filePath), new TypeReference<List<NotificationEvent>>() {});
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void saveAll(List<NotificationEvent> events) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), events);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public List<NotificationEvent> findByClientAndFilters(Instant from, Instant to, DeliveryStatus status) {
        return loadAll().stream()
                .filter(e -> !e.getCreatedAt().isBefore(from) && !e.getCreatedAt().isAfter(to))
                .filter(e -> e.getDeliveryStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<NotificationEvent> findById(String id) {
        return loadAll().stream().filter(e -> e.getId().equals(id)).findFirst();
    }

    @Override
    public List<NotificationEvent> findByStatus(DeliveryStatus status) {
        return loadAll().stream()
                .filter(e -> e.getDeliveryStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public void save(NotificationEvent event) {
        List<NotificationEvent> all = loadAll();
        all.removeIf(e -> e.getId().equals(event.getId()));
        all.add(event);
        saveAll(all);
    }
}
