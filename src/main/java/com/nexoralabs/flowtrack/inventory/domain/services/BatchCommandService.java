package com.nexoralabs.flowtrack.inventory.domain.services;

import com.nexoralabs.flowtrack.inventory.domain.model.aggregates.Batch;
import com.nexoralabs.flowtrack.inventory.domain.model.commands.CreateBatchCommand;
import com.nexoralabs.flowtrack.inventory.domain.model.commands.DeleteBatchCommand;
import com.nexoralabs.flowtrack.inventory.domain.model.commands.UpdateBatchCommand;

import java.util.Optional;

/**
 * Command service for Batch aggregate.
 * Handles creation and deletion of batches.
 */
public interface BatchCommandService {

    /**
     * Handle batch creation.
     * @param command create batch command
     * @return generated batch id
     */
    Long handle(CreateBatchCommand command);

    /**
     * Handle batch deletion.
     * @param command delete batch command
     */
    void handle(DeleteBatchCommand command);

    Optional<Batch> handle(UpdateBatchCommand command);

}
