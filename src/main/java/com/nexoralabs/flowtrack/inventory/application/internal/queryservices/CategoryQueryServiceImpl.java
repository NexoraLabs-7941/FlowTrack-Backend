package com.nexoralabs.flowtrack.inventory.application.internal.queryservices;

import com.nexoralabs.flowtrack.inventory.domain.model.aggregates.Category;
import com.nexoralabs.flowtrack.inventory.domain.model.queries.GetAllCategoriesQuery;
import com.nexoralabs.flowtrack.inventory.domain.services.CategoryQueryService;
import com.nexoralabs.flowtrack.inventory.infrastructure.internal.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * CategoryQueryService Implementation
 *
 * @summary
 * Implementation of the CategoryQueryService interface.
 * It is responsible for handling category queries.
 *
 * @since 1.0
 */
@Service
public class CategoryQueryServiceImpl implements CategoryQueryService {

    private final CategoryRepository categoryRepository;

    public CategoryQueryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Category> handle(GetAllCategoriesQuery query) {
        return categoryRepository.findAll();
    }
}

