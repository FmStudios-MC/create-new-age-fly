package org.antarcticgardens.cna.compat.computercraft.peripherals;

import com.zurrtum.create.compat.computercraft.implementation.peripherals.SyncedPeripheral;
import dan200.computercraft.api.lua.LuaFunction;
import org.antarcticgardens.cna.content.energising.EnergiserBlock;
import org.antarcticgardens.cna.content.energising.EnergiserBlockEntity;

public class EnergiserBlockEntityPeripheral extends SyncedPeripheral<EnergiserBlockEntity> {

    public EnergiserBlockEntityPeripheral(EnergiserBlockEntity blockEntity) {
        super(blockEntity);
    }

    @LuaFunction(mainThread = true)
    public final int getTier() {
        return blockEntity.tier;
    }

    @LuaFunction(mainThread = true)
    public final long getMaxEnergy() {
        return blockEntity.getEnergyStorage().getCapacity();
    }

    @LuaFunction(mainThread = true)
    public final long getCurrentEnergy() {
        return blockEntity.getEnergyStorage().getStoredEnergy();
    }

    @Override
    public String getType() {
        return "CNA_Energiser";
    }
}
