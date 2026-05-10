package com.nexoralabs.flowtrack.sales.domain.services;

import com.nexoralabs.flowtrack.sales.domain.model.commands.CreateSaleCommand;

public interface SaleCommandService {
    Long handle(CreateSaleCommand command);
}
