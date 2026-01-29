package com.acme.iiq.application.port.out;

import com.acme.iiq.domain.event.LifecycleEvent;

import java.util.List;

public interface PolicyEvaluatorPort {
    List<String> requiredApprovalsFor(LifecycleEvent event);
}
