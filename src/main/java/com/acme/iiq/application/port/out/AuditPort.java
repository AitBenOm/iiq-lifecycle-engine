package com.acme.iiq.application.port.out;

import java.util.Map;
import java.util.UUID;

public interface AuditPort {
    void append(UUID caseId, String eventType, Map<String, Object> details);
}
