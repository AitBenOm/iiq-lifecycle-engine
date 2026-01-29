package com.acme.iiq.application.model;

import com.acme.iiq.domain.event.LifecycleEventType;

import java.util.Map;
import java.util.Objects;

public record ReceiveLifecycleEventCommand(LifecycleEventType eventType,
                                          String identityId,
                                          Map<String, String> attributes) {
    public ReceiveLifecycleEventCommand {
        Objects.requireNonNull(eventType, "eventType is required");
        Objects.requireNonNull(identityId, "identityId is required");
        Objects.requireNonNull(attributes, "attributes is required");
    }
}
