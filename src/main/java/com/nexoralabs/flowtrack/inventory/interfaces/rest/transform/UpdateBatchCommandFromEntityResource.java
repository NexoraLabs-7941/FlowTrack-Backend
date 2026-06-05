package com.nexoralabs.flowtrack.inventory.interfaces.rest.transform;

import com.nexoralabs.flowtrack.inventory.domain.model.commands.UpdateBatchCommand;
import com.nexoralabs.flowtrack.inventory.interfaces.rest.resources.UpdateBatchResource;

public class UpdateBatchCommandFromEntityResource {
    public static UpdateBatchCommand toCommandFromResource(Long batchId, UpdateBatchResource resource) {
        if (batchId == null || batchId <= 0) {
            throw new IllegalArgumentException("batchId must be a positive number");
        }
        if (resource.quantity() < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        return new UpdateBatchCommand(
                batchId,
                resource.quantity()
        );
    }
}
