package com.nexoralabs.flowtrack.inventory.application.acl;

import com.nexoralabs.flowtrack.inventory.domain.model.aggregates.Batch;
import com.nexoralabs.flowtrack.inventory.domain.model.aggregates.Kit;
import com.nexoralabs.flowtrack.inventory.domain.model.commands.UpdateBatchCommand;
import com.nexoralabs.flowtrack.inventory.domain.model.queries.GetAllBatchesByProductIdQuery;
import com.nexoralabs.flowtrack.inventory.domain.model.queries.GetKitByIdQuery;
import com.nexoralabs.flowtrack.inventory.domain.model.queries.GetProductByIdQuery;
import com.nexoralabs.flowtrack.inventory.domain.services.BatchCommandService;
import com.nexoralabs.flowtrack.inventory.domain.services.BatchQueryService;
import com.nexoralabs.flowtrack.inventory.domain.services.KitQueryService;
import com.nexoralabs.flowtrack.inventory.domain.services.ProductCommandService;
import com.nexoralabs.flowtrack.integration.application.internal.PushNotificationService;
import com.nexoralabs.flowtrack.inventory.domain.services.ProductQueryService;
import com.nexoralabs.flowtrack.inventory.interfaces.acl.InventoryContextFacade;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryContextFacadeImpl implements InventoryContextFacade {

    private final ProductQueryService productQueryService;
    private final ProductCommandService productCommandService;
    private final BatchQueryService batchQueryService;
    private final BatchCommandService batchCommandService;
    private final KitQueryService kitQueryService;
    private final PushNotificationService pushNotificationService;


    public InventoryContextFacadeImpl(ProductQueryService productQueryService, ProductCommandService productCommandService, BatchQueryService batchQueryService, BatchCommandService batchCommandService, KitQueryService kitQueryService, PushNotificationService pushNotificationService) {
        this.productQueryService = productQueryService;
        this.productCommandService = productCommandService;
        this.batchQueryService = batchQueryService;
        this.batchCommandService = batchCommandService;
        this.kitQueryService = kitQueryService;
        this.pushNotificationService = pushNotificationService;
    }


    @Override
    public Long getProductById(Long productId) {
        var getProductByIdQuery = new GetProductByIdQuery(productId);
        var result = productQueryService.handle(getProductByIdQuery);
        return result.map(product -> product.getId()).orElse(null);

    }

    @Override
    public Boolean checkProductStockAvailability(Long productId, Integer requiredQuantity) {
        var getProductByIdQuery = new GetProductByIdQuery(productId);
        var result = productQueryService.handle(getProductByIdQuery);
        return result.map(product -> product.getMinStock() >= requiredQuantity).orElse(false);
    }

    @Override
    @Transactional
    public void decreaseStock(Long productId, Integer quantity) {
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("productId inválido");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("quantity inválida");
        }

        var getAllBatchesByProductIdQuery = new GetAllBatchesByProductIdQuery(productId);
        List<Batch> batches = batchQueryService.handle(getAllBatchesByProductIdQuery);

        List<Batch> sorted = batches.stream()
                .sorted(Comparator.comparing(Batch::getExpirationDate))
                .toList();

        int remaining = quantity;

        for (Batch batch : sorted) {
            if (remaining <= 0) break;
            int available = batch.getQuantity();
            if (available <= 0) continue;

            int toReduce = Math.min(available, remaining);
            int newQuantity = available - toReduce;

            var updateCommand = new UpdateBatchCommand(batch.getId(), newQuantity);
            var updated = batchCommandService.handle(updateCommand);
            if (updated == null || updated.isEmpty()) {
                throw new IllegalStateException("No se pudo actualizar el batch " + batch.getId());
            }

            remaining -= toReduce;
        }

        if (remaining > 0) {
            throw new IllegalStateException("Stock insuficiente. Faltan " + remaining + " unidades.");
        }

        // Trigger low stock notification if remaining total stock drops below 3
        try {
            List<Batch> updatedBatches = batchQueryService.handle(new GetAllBatchesByProductIdQuery(productId));
            int currentStock = updatedBatches.stream()
                    .mapToInt(Batch::getQuantity)
                    .sum();

            if (currentStock < 3) {
                var productOpt = productQueryService.handle(new GetProductByIdQuery(productId));
                String productName = productOpt.map(p -> p.getName()).orElse("Producto #" + productId);
                
                String alertTitle = "Stock Bajo";
                String alertBody = "El producto " + productName + " tiene stock bajo (" + currentStock + " unidades).";
                
                pushNotificationService.sendPushToAllUsers(alertTitle, alertBody, "alert");
            }
        } catch (Exception e) {
            // Prevent notification errors from rolling back the stock decrease transaction
        }
    }

    @Override
    public Double getProductUnitPrice(Long productId) {
        var getProductByIdQuery = new GetProductByIdQuery(productId);
        var result = productQueryService.handle(getProductByIdQuery);
        return result.map(product -> product.getUnitPrice()).orElse(null);
    }

    @Override
    public Long getKitById(Long kitId) {
        var getKitByIdQuery = new GetKitByIdQuery(kitId);
        var result = kitQueryService.handle(getKitByIdQuery);
        return result.map(Kit::getId).orElse(null);
    }

    @Override
    public Double getKitTotalPrice(Long kitId) {
        var getKitByIdQuery = new GetKitByIdQuery(kitId);
        var result = kitQueryService.handle(getKitByIdQuery);
        if (result.isEmpty()) {
            return null;
        }
        Kit kit = result.get();
        // Calculate total price: sum of (unit price * quantity) for each item
        // The price in KitItem is unit price, so we multiply by quantity
        return kit.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    @Override
    public List<Object[]> getKitProductIdsQuantitiesAndPrices(Long kitId) {
        var getKitByIdQuery = new GetKitByIdQuery(kitId);
        var result = kitQueryService.handle(getKitByIdQuery);
        if (result.isEmpty()) {
            return List.of();
        }
        Kit kit = result.get();
        // Return list of [productId, quantity, price] arrays
        return kit.getItems().stream()
                .map(item -> new Object[]{
                    item.getProductId(), 
                    item.getQuantity(), 
                    item.getPrice()
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void decreaseStockForKit(Long kitId, Integer kitQuantity) {
        if (kitId == null || kitId <= 0) {
            throw new IllegalArgumentException("kitId inválido");
        }
        if (kitQuantity == null || kitQuantity <= 0) {
            throw new IllegalArgumentException("kitQuantity inválida");
        }

        // Get all products, quantities and prices from the kit
        List<Object[]> kitItems = getKitProductIdsQuantitiesAndPrices(kitId);
        if (kitItems.isEmpty()) {
            throw new IllegalArgumentException("Kit no encontrado: " + kitId);
        }

        // For each item in the kit, decrease stock by (item quantity * kit quantity)
        for (Object[] item : kitItems) {
            Long productId = (Long) item[0];
            Integer itemQuantity = (Integer) item[1];
            int totalQuantity = itemQuantity * kitQuantity;
            decreaseStock(productId, totalQuantity);
        }
    }

//    @Override
//    public boolean existsProductById(Long productId) {
//        var getProductByIdQuery = new GetProductByIdQuery(productId);
//        var result = productQueryService.handle(getProductByIdQuery);
//        return result.isPresent();
//    }
}
