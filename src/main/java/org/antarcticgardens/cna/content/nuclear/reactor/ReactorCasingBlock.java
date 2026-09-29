package org.antarcticgardens.cna.content.nuclear.reactor;

import com.zurrtum.create.content.decoration.encasing.CasingBlock;
import com.zurrtum.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ReactorCasingBlock extends CasingBlock {
    public ReactorCasingBlock(Properties properties) {
        super(properties.strength(6.0f));
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        IBE.onRemove(state, level, pos, newState);
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
