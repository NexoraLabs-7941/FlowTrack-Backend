package com.nexoralabs.flowtrack.inventory.application.internal.queryservices;

import com.nexoralabs.flowtrack.inventory.domain.model.aggregates.Kit;
import com.nexoralabs.flowtrack.inventory.domain.model.queries.GetAllKitsQuery;
import com.nexoralabs.flowtrack.inventory.domain.model.queries.GetKitByIdQuery;
import com.nexoralabs.flowtrack.inventory.domain.services.KitQueryService;
import com.nexoralabs.flowtrack.inventory.infrastructure.persistence.jpa.repositories.KitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * KitQueryService Implementation
 * 
 * @summary
 * Implementation of the KitQueryService interface.
 * It is responsible for handling kit queries.
 * 
 * @since 1.0
 */
@Service
@Transactional(readOnly = true)
public class KitQueryServiceImpl implements KitQueryService {

    private final KitRepository kitRepository;

    public KitQueryServiceImpl(KitRepository kitRepository) {
        this.kitRepository = kitRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Kit> handle(GetAllKitsQuery query) {
        return kitRepository.findAllWithItems();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Kit> handle(GetKitByIdQuery query) {
        return kitRepository.findByIdWithItems(query.kitId());
    }
}

