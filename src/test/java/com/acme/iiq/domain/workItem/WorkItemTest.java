package com.acme.iiq.domain.workitem;

import com.acme.iiq.domain.workItem.Decision;
import com.acme.iiq.domain.workItem.WorkItem;
import com.acme.iiq.domain.workItem.WorkItemStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WorkItemTest {

    @Test
    void new_work_item_is_pending() {
        WorkItem wi = new WorkItem(UUID.randomUUID(), "Manager Approval");

        assertEquals(WorkItemStatus.PENDING, wi.getStatus());
    }

    @Test
    void decide_approve_moves_to_approved() {
        WorkItem wi = new WorkItem(UUID.randomUUID(), "Manager Approval");

        wi.decide(Decision.APPROVE);

        assertEquals(WorkItemStatus.APPROVED, wi.getStatus());
        assertTrue(wi.isApproved());
    }

    @Test
    void decide_reject_moves_to_rejected() {
        WorkItem wi = new WorkItem(UUID.randomUUID(), "Manager Approval");

        wi.decide(Decision.REJECT);

        assertEquals(WorkItemStatus.REJECTED, wi.getStatus());
        assertFalse(wi.isApproved());
    }

    @Test
    void cannot_decide_twice() {
        WorkItem wi = new WorkItem(UUID.randomUUID(), "Manager Approval");
        wi.decide(Decision.APPROVE);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> wi.decide(Decision.REJECT));

        assertTrue(ex.getMessage().toLowerCase().contains("already"));
    }
}
