package com.nexoralabs.flowtrack.inventory.interfaces.rest.transform;

import com.nexoralabs.flowtrack.inventory.domain.model.commands.CreateKitCommand;
import com.nexoralabs.flowtrack.inventory.interfaces.rest.resources.CreateKitResource;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Assembler to create a CreateKitCommand from a CreateKitResource.
 * @since 1.0
 */
public class CreateKitCommandFromResourceAssembler {
    /**
     * Converts a CreateKitResource to a CreateKitCommand.
     * @param resource CreateKitResource to convert
     * @return CreateKitCommand created from the resource
     */
    public static CreateKitCommand toCommandFromResource(CreateKitResource resource) {
        List<CreateKitCommand.KitItemCommand> items = resource.items().stream()
                .map(item -> new CreateKitCommand.KitItemCommand(item.productId(), item.quantity(), item.price()))
                .collect(Collectors.toList());
        
        return new CreateKitCommand(resource.name(), items);
    }
}

