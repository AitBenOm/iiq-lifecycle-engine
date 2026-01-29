package com.acme.iiq.application.usecase;

import com.acme.iiq.application.model.ReceiveLifecycleEventCommand;
import com.acme.iiq.application.port.out.AuditPort;
import com.acme.iiq.application.port.out.LifecycleCasePort;
import com.acme.iiq.application.port.out.PolicyEvaluatorPort;
import com.acme.iiq.application.port.out.WorkItemPort;
import com.acme.iiq.domain.caseFile.CaseStatus;
import com.acme.iiq.domain.caseFile.LifecycleCase;
import com.acme.iiq.domain.event.LifecycleEventType;
import com.acme.iiq.domain.workItem.WorkItem;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ReceiveLifecycleEventServiceTest {

    @Test
    void creates_case_and_work_items_based_on_policy() {
        InMemoryCasePort casePort = new InMemoryCasePort();
        CapturingWorkItemPort workItemPort = new CapturingWorkItemPort();
        PolicyEvaluatorPort policyPort = event -> List.of("Manager Approval", "IGA Approval");
        CapturingAuditPort auditPort = new CapturingAuditPort();

        ReceiveLifecycleEventService service = new ReceiveLifecycleEventService(
                casePort, workItemPort, policyPort, auditPort
        );

        var cmd = new ReceiveLifecycleEventCommand(
                LifecycleEventType.JOINER,
                "jsmith",
                Map.of("department", "IT")
        );

        var result = service.handle(cmd);

        assertNotNull(result.caseId());
        assertEquals(CaseStatus.WAITING_APPROVAL, result.status());

        assertTrue(casePort.saved.containsKey(result.caseId()));
        assertEquals(2, workItemPort.savedWorkItems.size());

        assertEquals("CASE_CREATED", auditPort.lastEventType);
        assertEquals("jsmith", auditPort.lastDetails.get("identityId"));
    }

    // ---- fakes ----

    static final class InMemoryCasePort implements LifecycleCasePort {
        final Map<UUID, LifecycleCase> saved = new HashMap<>();

        @Override
        public void save(LifecycleCase lifecycleCase) {
            saved.put(lifecycleCase.getCaseId(), lifecycleCase);
        }

        @Override
        public Optional<LifecycleCase> findById(UUID caseId) {
            return Optional.ofNullable(saved.get(caseId));
        }
    }

    static final class CapturingWorkItemPort implements WorkItemPort {
        final List<WorkItem> savedWorkItems = new ArrayList<>();

        @Override
        public void saveAll(UUID caseId, List<WorkItem> workItems) {
            savedWorkItems.clear();
            savedWorkItems.addAll(workItems);
        }
    }

    static final class CapturingAuditPort implements AuditPort {
        UUID lastCaseId;
        String lastEventType;
        Map<String, Object> lastDetails;

        @Override
        public void append(UUID caseId, String eventType, Map<String, Object> details) {
            this.lastCaseId = caseId;
            this.lastEventType = eventType;
            this.lastDetails = details;
        }
    }
}
