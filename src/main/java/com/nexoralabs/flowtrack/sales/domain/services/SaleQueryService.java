package com.nexoralabs.flowtrack.sales.domain.services;

import com.nexoralabs.flowtrack.sales.domain.model.aggregates.Sale;
import com.nexoralabs.flowtrack.sales.domain.model.queries.GetAllSalesQuery;
import com.nexoralabs.flowtrack.sales.domain.model.queries.GetSaleByIdQuery;

import java.util.List;
import java.util.Optional;

public interface SaleQueryService {

    Optional<Sale> handle(GetSaleByIdQuery getSaleByIdQuery);

    List<Sale> handle(GetAllSalesQuery getAllSalesQuery);
}
