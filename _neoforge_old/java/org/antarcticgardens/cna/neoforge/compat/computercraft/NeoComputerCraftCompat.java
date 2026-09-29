package org.antarcticgardens.cna.neoforge.compat.computercraft;

import com.simibubi.create.compat.Mods;
import dan200.computercraft.api.peripheral.PeripheralCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.antarcticgardens.cna.CNABlockEntityTypes;

public class NeoComputerCraftCompat {

    public static void registerComputerCraftCapabilities(RegisterCapabilitiesEvent event) {
        if (Mods.COMPUTERCRAFT.isLoaded()) {
            event.registerBlockEntity(
                    PeripheralCapability.get(),
                    CNABlockEntityTypes.ENERGISER.get(),
                    (be, context) -> be.computerBehaviour.getPeripheralCapability()
            );

            event.registerBlockEntity(
                    PeripheralCapability.get(),
                    CNABlockEntityTypes.BASIC_MOTOR.get(),
                    (be, context) -> be.computerBehaviour.getPeripheralCapability()
            );

            event.registerBlockEntity(
                    PeripheralCapability.get(),
                    CNABlockEntityTypes.ADVANCED_MOTOR.get(),
                    (be, context) -> be.computerBehaviour.getPeripheralCapability()
            );

            event.registerBlockEntity(
                    PeripheralCapability.get(),
                    CNABlockEntityTypes.REINFORCED_MOTOR.get(),
                    (be, context) -> be.computerBehaviour.getPeripheralCapability()
            );

            event.registerBlockEntity(
                    PeripheralCapability.get(),
                    CNABlockEntityTypes.CARBON_BRUSHES.get(),
                    (be, context) -> be.computerBehaviour.getPeripheralCapability()
            );
        }
    }

}
