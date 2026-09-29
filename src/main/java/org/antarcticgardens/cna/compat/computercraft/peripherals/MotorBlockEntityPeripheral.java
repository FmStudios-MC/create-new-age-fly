package org.antarcticgardens.cna.compat.computercraft.peripherals;

import com.zurrtum.create.compat.computercraft.implementation.peripherals.SyncedPeripheral;
import dan200.computercraft.api.lua.LuaFunction;
import org.antarcticgardens.cna.content.motor.MotorBlockEntity;

public class MotorBlockEntityPeripheral  extends SyncedPeripheral<MotorBlockEntity>  {
    public MotorBlockEntityPeripheral(MotorBlockEntity blockEntity) {
        super(blockEntity);
    }

    @LuaFunction(mainThread = true)
    public int getTier()
    {
        return blockEntity.tier;
    }

    @Override
    public String getType() {
        return "CNA_motor";
    }
}
