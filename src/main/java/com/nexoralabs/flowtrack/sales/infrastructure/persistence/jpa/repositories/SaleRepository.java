package com.nexoralabs.flowtrack.sales.infrastructure.persistence.jpa.repositories;

import com.nexoralabs.flowtrack.sales.domain.model.aggregates.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, Long> {

}
