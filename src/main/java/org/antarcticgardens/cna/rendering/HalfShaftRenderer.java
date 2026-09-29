package org.antarcticgardens.cna.rendering;

import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class HalfShaftRenderer extends KineticBlockEntityRenderer<KineticBlockEntity> {
    public HalfShaftRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(KineticBlockEntity blockEntity, BlockState blockState) {
        return CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, blockState);
    }
}
