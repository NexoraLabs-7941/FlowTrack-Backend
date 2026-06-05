package com.nexoralabs.flowtrack.sales.application.internal.queryservices;

import com.nexoralabs.flowtrack.sales.domain.model.aggregates.Sale;
import com.nexoralabs.flowtrack.sales.domain.model.queries.GetAllSalesQuery;
import com.nexoralabs.flowtrack.sales.domain.model.queries.GetSaleByIdQuery;
import com.nexoralabs.flowtrack.sales.domain.services.SaleQueryService;
import com.nexoralabs.flowtrack.sales.infrastructure.persistence.jpa.repositories.SaleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SaleQueryServiceImpl implements SaleQueryService {

    private final SaleRepository saleRepository;

    public SaleQueryServiceImpl(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    @Override
    public Optional<Sale> handle(GetSaleByIdQuery query) {
        return saleRepository.findById(query.saleId());
    }

    @Override
    public List<Sale> handle(GetAllSalesQuery query) {
        return saleRepository.findAll();
    }
}
