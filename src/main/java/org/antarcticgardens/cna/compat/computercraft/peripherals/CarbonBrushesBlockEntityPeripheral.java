package org.antarcticgardens.cna.compat.computercraft.peripherals;

import com.zurrtum.create.compat.computercraft.implementation.peripherals.SyncedPeripheral;
import dan200.computercraft.api.lua.LuaFunction;
import org.antarcticgardens.cna.content.electricity.generation.brushes.CarbonBrushesBlockEntity;

public class CarbonBrushesBlockEntityPeripheral extends SyncedPeripheral<CarbonBrushesBlockEntity> {
    public CarbonBrushesBlockEntityPeripheral(CarbonBrushesBlockEntity blockEntity) {
        super(blockEntity);
    }

    @LuaFunction(mainThread = true)
    public final long getOutput() {
        return blockEntity.getEnergyStorage().getCapacity() / 20L;
    }

    @Override
    public String getType() {
        return "CNA_carbon_brushes";
    }
}
