package org.antarcticgardens.cna.content.heat.pipe;

import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.world.level.storage.ValueOutput;

import net.minecraft.world.level.storage.ValueInput;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.antarcticgardens.cna.content.heat.HeatBlockEntity;

import java.util.List;

public class HeatPipeBlockEntity extends SmartBlockEntity implements HeatBlockEntity {
    public float generating;

    public HeatPipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
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
        generating = tag.getFloatOr("generating", 0f);
    }

    @Override
    protected void write(ValueOutput tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putFloat("heat", heat);
        tag.putFloat("generating", generating);
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



}
