package com.acme.iiq.domain.event;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record LifecycleEvent(
        UUID eventId,
        LifecycleEventType type,
        String identityId,
        Map<String, String> attributes,
        Instant occurredAt


) {
    public LifecycleEvent {
        Objects.requireNonNull(eventId, "eventId is required");
        Objects.requireNonNull(type, "type is required");
        Objects.requireNonNull(identityId, "identityId is required");
        Objects.requireNonNull(attributes, "attributes is required");
        Objects.requireNonNull(occurredAt, "occurredAt is required");
    }
}
