package org.antarcticgardens.cna.util;

import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;

public final class SmartTicker {
    private SmartTicker() {
    }

    /**
     * Runs {@link SmartBlockEntity#tick()} every tick before the block's own ticker. The heat block
     * entities became smart block entities in the port, because Create Fly finds goggle tooltips
     * through client behaviours, and those are only attached from the first smart tick.
     */
    public static <T extends BlockEntity> BlockEntityTicker<T> wrap(BlockEntityTicker<T> ticker) {
        return (level, pos, state, blockEntity) -> {
            if (blockEntity instanceof SmartBlockEntity smart)
                smart.tick();
            ticker.tick(level, pos, state, blockEntity);
        };
    }
}
