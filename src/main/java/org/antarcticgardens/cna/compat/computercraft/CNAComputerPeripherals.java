package org.antarcticgardens.cna.compat.computercraft;

import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.compat.computercraft.AbstractComputerBehaviour;
import com.zurrtum.create.compat.computercraft.implementation.ComputerBehaviour;
import com.zurrtum.create.compat.computercraft.implementation.peripherals.SyncedPeripheral;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import dan200.computercraft.api.peripheral.PeripheralLookup;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.antarcticgardens.cna.CNABlockEntityTypes;
import org.antarcticgardens.cna.compat.computercraft.peripherals.CarbonBrushesBlockEntityPeripheral;
import org.antarcticgardens.cna.compat.computercraft.peripherals.EnergiserBlockEntityPeripheral;
import org.antarcticgardens.cna.compat.computercraft.peripherals.MotorBlockEntityPeripheral;

import java.util.function.Function;

/**
 * Same wiring as Create Fly's AllComputerPeripherals: a computer behaviour attached per block entity
 * type, and the peripheral handed to CC: Tweaked's lookup. Upstream added the behaviour from each
 * block entity's addBehaviours through a proxy. Only call this when CC: Tweaked is loaded.
 */
public class CNAComputerPeripherals {
    private static <T extends SmartBlockEntity> void registerPeripheral(BlockEntityType<T> type, Function<T, SyncedPeripheral<T>> factory) {
        BlockEntityBehaviour.add(type, ComputerBehaviour::new);
        PeripheralLookup.get().registerForBlockEntity((blockEntity, direction) -> {
            if (blockEntity.getBehaviour(AbstractComputerBehaviour.TYPE) instanceof ComputerBehaviour behaviour) {
                if (behaviour.peripheral == null)
                    behaviour.peripheral = factory.apply(blockEntity);
                return behaviour.peripheral;
            }
            return null;
        }, type);
    }

    public static void register() {
        registerPeripheral(CNABlockEntityTypes.BASIC_MOTOR, MotorBlockEntityPeripheral::new);
        registerPeripheral(CNABlockEntityTypes.ADVANCED_MOTOR, MotorBlockEntityPeripheral::new);
        registerPeripheral(CNABlockEntityTypes.REINFORCED_MOTOR, MotorBlockEntityPeripheral::new);
        registerPeripheral(CNABlockEntityTypes.ENERGISER, EnergiserBlockEntityPeripheral::new);
        registerPeripheral(CNABlockEntityTypes.CARBON_BRUSHES, CarbonBrushesBlockEntityPeripheral::new);
    }
}
