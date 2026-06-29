package com.nexoralabs.flowtrack.sales.interfaces.rest.transform;

import com.nexoralabs.flowtrack.sales.domain.model.aggregates.Sale;
import com.nexoralabs.flowtrack.sales.interfaces.rest.resources.SaleDetailResource;
import com.nexoralabs.flowtrack.sales.interfaces.rest.resources.SaleResource;

import java.util.stream.Collectors;

public class SaleResourceFromEntityAssembler {

    public static SaleResource toResourceFromEntity(Sale entity) {
        var detailsResource = entity.getDetails().stream()
                .map(detail -> new SaleDetailResource(
                        detail.getId(),
                        detail.getProductId().id(),
                        detail.getQuantity(),
                        detail.getUnitPrice(),
                        detail.getTotalPrice()
                )).collect(Collectors.toList());


        return new SaleResource(
                entity.getId(),
                entity.getTotalAmount(),
                entity.getCreatedAt(),
                detailsResource
        );
    }
}
