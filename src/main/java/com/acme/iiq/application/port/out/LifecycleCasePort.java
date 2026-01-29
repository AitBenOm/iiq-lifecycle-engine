package com.acme.iiq.application.port.out;

import com.acme.iiq.domain.caseFile.LifecycleCase;

import java.util.Optional;
import java.util.UUID;

public interface LifecycleCasePort {
    void save(LifecycleCase lifecycleCase);
    Optional<LifecycleCase> findById(UUID caseId);
}
