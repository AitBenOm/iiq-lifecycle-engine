package com.acme.iiq.application.usecase;

import com.acme.iiq.application.model.ReceiveLifecycleEventCommand;
import com.acme.iiq.application.model.ReceiveLifecycleEventResult;
import com.acme.iiq.application.port.in.ReceiveLifecycleEventUseCase;
import com.acme.iiq.application.port.out.AuditPort;
import com.acme.iiq.application.port.out.LifecycleCasePort;
import com.acme.iiq.application.port.out.PolicyEvaluatorPort;
import com.acme.iiq.application.port.out.WorkItemPort;
import com.acme.iiq.domain.caseFile.LifecycleCase;
import com.acme.iiq.domain.event.LifecycleEvent;
import com.acme.iiq.domain.workItem.WorkItem;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class ReceiveLifecycleEventService implements ReceiveLifecycleEventUseCase {

    private final LifecycleCasePort casePort;
    private final WorkItemPort workItemPort;
    private final PolicyEvaluatorPort policyEvaluatorPort;
    private final AuditPort auditPort;

    public ReceiveLifecycleEventService(
            LifecycleCasePort casePort,
            WorkItemPort workItemPort,
            PolicyEvaluatorPort policyEvaluatorPort,
            AuditPort auditPort
    ) {
        this.casePort = casePort;
        this.workItemPort = workItemPort;
        this.policyEvaluatorPort = policyEvaluatorPort;
        this.auditPort = auditPort;
    }

    @Override
    public ReceiveLifecycleEventResult handle(ReceiveLifecycleEventCommand command) {

        LifecycleEvent event = new LifecycleEvent(
                UUID.randomUUID(),
                command.eventType(),
                command.identityId(),
                command.attributes(),
                Instant.now()
        );

        LifecycleCase lifecycleCase = LifecycleCase.openFromEvent(event);

        List<String> approvals = policyEvaluatorPort.requiredApprovalsFor(event);

        for (String approvalName : approvals) {
            WorkItem wi = new WorkItem(UUID.randomUUID(), approvalName);
            lifecycleCase.addWorkItem(wi);
        }

        casePort.save(lifecycleCase);
        workItemPort.saveAll(lifecycleCase.getCaseId(), lifecycleCase.getWorkItems());

        auditPort.append(
                lifecycleCase.getCaseId(),
                "CASE_CREATED",
                Map.of(
                        "identityId", command.identityId(),
                        "eventType", command.eventType().name(),
                        "approvals", approvals
                )
        );

        return new ReceiveLifecycleEventResult(lifecycleCase.getCaseId(), lifecycleCase.getStatus());
    }
}
