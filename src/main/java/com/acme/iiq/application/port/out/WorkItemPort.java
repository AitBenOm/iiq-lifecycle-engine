package com.acme.iiq.application.port.out;

import com.acme.iiq.domain.workItem.WorkItem;

import java.util.List;
import java.util.UUID;

public interface WorkItemPort {
    void saveAll(UUID caseId, List<WorkItem> workItems);

}
