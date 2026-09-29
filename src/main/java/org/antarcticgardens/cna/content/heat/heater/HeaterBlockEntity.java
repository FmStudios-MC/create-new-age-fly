package org.antarcticgardens.cna.content.heat.heater;

import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.world.level.storage.ValueOutput;

import net.minecraft.world.level.storage.ValueInput;

import com.zurrtum.create.content.processing.burner.BlazeBurnerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.antarcticgardens.cna.config.CNAConfig;
import org.antarcticgardens.cna.content.heat.HeatBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HeaterBlockEntity extends SmartBlockEntity implements HeatBlockEntity {
    public HeaterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour<?>> behaviours) {
    }

    public float heat = 0;

    @Override
    protected void read(ValueInput tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        heat = tag.getFloatOr("heat", 0f);
    }

    @Override
    protected void write(ValueOutput tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putFloat("heat", heat);
    }

    @Override
    public boolean canConnect(Direction from) {
        return from != Direction.DOWN;
    }


    @Override
    public float getHeat() {
        return heat;
    }

    @Override
    public void addHeat(float amount) {
        heat += amount;
        setChanged();
    }

    @Override
    public void setHeat(float amount) {
        heat = amount;
        setChanged();
    }



    @Nullable
    @Override
    public float[] getHeatTiers() {
        return new float[] {
                50,
                100,
                400,
                500
        };
    }

    @Override
    public float getTierHeat() {
        BlazeBurnerBlock.HeatLevel strength = getBlockState().getValue(HeaterBlock.STRENGTH);
        double heat = 0;
        Double mult = CNAConfig.getServer().heaterRequiredHeatMultiplier.get();
        switch (strength) {
            case NONE -> {
                heat = 0;
            }
            case SMOULDERING -> {
                heat = 50 * mult;
            }
            case FADING -> {
                heat = 100 * mult;
            }
            case KINDLED -> {
                heat = 400 * mult;
            }
            case SEETHING -> {
                heat = 500 * mult;
            }
        }
        return (float)heat;
    }

    @Override
    public double getHeatTierMultiplier() {
        return CNAConfig.getServer().heaterRequiredHeatMultiplier.get();
    }

}
