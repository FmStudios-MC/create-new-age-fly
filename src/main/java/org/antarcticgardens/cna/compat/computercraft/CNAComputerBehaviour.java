package org.antarcticgardens.cna.compat.computercraft;

import com.zurrtum.create.compat.computercraft.AbstractComputerBehaviour;
import com.zurrtum.create.compat.computercraft.implementation.peripherals.SyncedPeripheral;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.registries.BuiltInRegistries;
import org.antarcticgardens.cna.compat.computercraft.peripherals.CarbonBrushesBlockEntityPeripheral;
import org.antarcticgardens.cna.compat.computercraft.peripherals.EnergiserBlockEntityPeripheral;
import org.antarcticgardens.cna.compat.computercraft.peripherals.MotorBlockEntityPeripheral;
import org.antarcticgardens.cna.content.electricity.generation.brushes.CarbonBrushesBlockEntity;
import org.antarcticgardens.cna.content.energising.EnergiserBlockEntity;
import org.antarcticgardens.cna.content.motor.MotorBlockEntity;

import java.util.function.Supplier;

public class CNAComputerBehaviour extends AbstractComputerBehaviour {
    SyncedPeripheral<?> peripheral;
    Supplier<SyncedPeripheral<?>> peripheralSupplier;

    public CNAComputerBehaviour(SmartBlockEntity be) {
        super(be);
        this.peripheralSupplier = getPeripheralSupplier(be);
    }

    public static Supplier<SyncedPeripheral<?>> getPeripheralSupplier(SmartBlockEntity be) {
        if (be instanceof EnergiserBlockEntity energiserBlockEntity)
            return () -> new EnergiserBlockEntityPeripheral(energiserBlockEntity);
        if (be instanceof MotorBlockEntity motorBlockEntity)
            return () -> new MotorBlockEntityPeripheral(motorBlockEntity);
        if (be instanceof CarbonBrushesBlockEntity carbonBrushesBlockEntity)
            return () -> new CarbonBrushesBlockEntityPeripheral(carbonBrushesBlockEntity);

        throw new IllegalArgumentException(
                "No peripheral registered for " + BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(be.getType()));
    }

    @Override
    public IPeripheral getPeripheralCapability() {
        if (peripheral == null)
            peripheral = peripheralSupplier.get();
        return peripheral;
    }
}
