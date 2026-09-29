package org.antarcticgardens.cna.content.heat.pipe;

import com.zurrtum.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class ReactorEncasedHeatPipeBlock extends EncasedHeatPipeBlock{

    public ReactorEncasedHeatPipeBlock(Properties properties, Supplier<Block> casing) {
        super(properties.strength(6.0f), casing);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        IBE.onRemove(state, level, pos, newState);
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
