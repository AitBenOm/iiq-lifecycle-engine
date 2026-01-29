package com.acme.iiq.domain.caseFile;

import com.acme.iiq.domain.workItem.WorkItem;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class LifecycleCase {
    private final UUID caseId;
    private final String identityId;
    private final CaseType caseType;
    private CaseStatus status;
    private Instant createdAt;
    private final List<WorkItem> workItems = new ArrayList<>();

    public LifecycleCase(UUID caseId, String identityId, CaseType caseType, CaseStatus status, Instant createdAt) {
        this.caseId = Objects.requireNonNull(caseId);
        this.identityId = Objects.requireNonNull(identityId);
        this.caseType = Objects.requireNonNull(caseType);
        this.status = Objects.requireNonNull(status);
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public void addWorkItem(WorkItem item) {
        if (status != CaseStatus.OPEN && status != CaseStatus.WAITING_APPROVAL) {
            throw new IllegalStateException("Cannot add work item when case is " + status);
        }
        this.workItems.add(item);
        this.status = CaseStatus.WAITING_APPROVAL;
    }

    public void markApproved() {
        if (workItems.stream().anyMatch(wi -> !wi.isApproved())) {
            throw new IllegalStateException("Cannot approve case: not all work items are approved");
        }
        this.status = CaseStatus.APPROVED;
    }

    public void markRejected() {
        this.status = CaseStatus.REJECTED;
    }

    public UUID getCaseId() {
        return caseId;
    }

    public CaseStatus getStatus() {
        return status;
    }

    public List<WorkItem> getWorkItems() {
        return List.copyOf(workItems);
    }
}
