package com.nexoralabs.flowtrack.sales.domain.model.commands;

public record SaleDetailItem(Long productId, int quantity, double unitPrice) {}
