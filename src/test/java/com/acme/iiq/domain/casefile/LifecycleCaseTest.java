package com.acme.iiq.domain.casefile;

import com.acme.iiq.domain.caseFile.CaseStatus;
import com.acme.iiq.domain.caseFile.LifecycleCase;
import com.acme.iiq.domain.event.LifecycleEvent;
import com.acme.iiq.domain.event.LifecycleEventType;
import com.acme.iiq.domain.workItem.Decision;
import com.acme.iiq.domain.workItem.WorkItem;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LifecycleCaseTest {

    @Test
    void open_from_event_creates_open_case() {
        LifecycleEvent event = new LifecycleEvent(
                UUID.randomUUID(),
                LifecycleEventType.JOINER,
                "jsmith",
                Map.of("department", "IT"),
                Instant.now()
        );

        LifecycleCase caze = LifecycleCase.openFromEvent(event);

        assertNotNull(caze.getCaseId());
        assertEquals(CaseStatus.OPEN, caze.getStatus());
        assertTrue(caze.getWorkItems().isEmpty());
    }

    @Test
    void adding_work_item_moves_case_to_waiting_approval() {
        LifecycleCase caze = LifecycleCase.openFromEvent(sampleEvent());
        WorkItem wi = new WorkItem(UUID.randomUUID(), "Manager Approval");

        caze.addWorkItem(wi);

        assertEquals(CaseStatus.WAITING_APPROVAL, caze.getStatus());
        assertEquals(1, caze.getWorkItems().size());
    }

    @Test
    void cannot_mark_approved_if_any_work_item_not_approved() {
        LifecycleCase caze = LifecycleCase.openFromEvent(sampleEvent());
        WorkItem wi = new WorkItem(UUID.randomUUID(), "Manager Approval");
        caze.addWorkItem(wi);

        IllegalStateException ex = assertThrows(IllegalStateException.class, caze::markApproved);
        assertTrue(ex.getMessage().toLowerCase().contains("not all"));
    }

    @Test
    void mark_approved_when_all_work_items_approved() {
        LifecycleCase caze = LifecycleCase.openFromEvent(sampleEvent());

        WorkItem wi1 = new WorkItem(UUID.randomUUID(), "Manager Approval");
        WorkItem wi2 = new WorkItem(UUID.randomUUID(), "IGA Approval");

        caze.addWorkItem(wi1);
        caze.addWorkItem(wi2);

        wi1.decide(Decision.APPROVE);
        wi2.decide(Decision.APPROVE);

        caze.markApproved();

        assertEquals(CaseStatus.APPROVED, caze.getStatus());
    }

    @Test
    void can_reject_case() {
        LifecycleCase caze = LifecycleCase.openFromEvent(sampleEvent());

        caze.markRejected();

        assertEquals(CaseStatus.REJECTED, caze.getStatus());
    }

    private static LifecycleEvent sampleEvent() {
        return new LifecycleEvent(
                UUID.randomUUID(),
                LifecycleEventType.MOVER,
                "jsmith",
                Map.of("title", "Senior Engineer"),
                Instant.now()
        );
    }
}
