package com.acme.iiq.application.port.in;

import com.acme.iiq.application.model.ReceiveLifecycleEventCommand;
import com.acme.iiq.application.model.ReceiveLifecycleEventResult;

public interface ReceiveLifecycleEventUseCase {

    ReceiveLifecycleEventResult handle(ReceiveLifecycleEventCommand commande);
}
