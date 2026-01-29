package com.acme.iiq.application.model;

import com.acme.iiq.domain.caseFile.CaseStatus;

import java.util.Objects;
import java.util.UUID;

public record ReceiveLifecycleEventResult(
        UUID caseId,
        CaseStatus status
) {
    public ReceiveLifecycleEventResult {
        Objects.requireNonNull(caseId, "caseId is required");
        Objects.requireNonNull(status, "status is required");
    }
}
