package com.nexoralabs.flowtrack.inventory.infrastructure.persistence.jpa.repositories;

import com.nexoralabs.flowtrack.inventory.domain.model.aggregates.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {}
