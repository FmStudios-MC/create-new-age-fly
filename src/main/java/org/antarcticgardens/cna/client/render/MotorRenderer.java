package org.antarcticgardens.cna.client.render;

import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import org.antarcticgardens.cna.content.motor.MotorBlockEntity;

/** The motor's half shaft when Flywheel is off. Was {@code HalfShaftRenderer}; same as Create Fly's creative motor. */
public class MotorRenderer extends KineticBlockEntityRenderer<MotorBlockEntity> {
    public MotorRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(MotorBlockEntity be, BlockState state) {
        return CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, state);
    }
}
