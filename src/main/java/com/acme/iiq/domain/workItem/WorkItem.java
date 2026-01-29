package com.acme.iiq.domain.workItem;

import com.acme.iiq.domain.workItem.WorkItemStatus;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class WorkItem {

    private final UUID workItemId;
    private final String name;

    private WorkItemStatus status;

    private Decision decision;
    private Instant decidedAt;


    public WorkItem(UUID workItemId, String name, WorkItemStatus status, Decision decision, Instant decidedAt) {
        this.workItemId = Objects.requireNonNull(workItemId);
        this.name = Objects.requireNonNull(name);
        this.status = WorkItemStatus.PENDING;
    }


    public void decide(Decision decision) {
        if (this.status != WorkItemStatus.PENDING) {
            throw new IllegalStateException("WorkItem already decided");
        }

        this.status = decision == Decision.APPROVE ? WorkItemStatus.APPROVED.APPROVED : WorkItemStatus.REJECTED;
        this.decidedAt = Instant.now();
    }

    public boolean isApproved() {
        return status == WorkItemStatus.APPROVED;
    }

    public UUID getWorkItemId() {
        return workItemId;
    }

    public WorkItemStatus getStatus() {
        return status;
    }
}
