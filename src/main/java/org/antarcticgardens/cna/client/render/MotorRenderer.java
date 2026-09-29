package org.antarcticgardens.cna.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer;
import com.zurrtum.create.client.content.kinetics.base.SingleKineticRenderState;
import com.zurrtum.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.antarcticgardens.cna.content.motor.MotorBlockEntity;
import org.jetbrains.annotations.Nullable;

/** The motor's half shaft when Flywheel is off. Was {@code HalfShaftRenderer}; same as Create Fly's creative motor. */
public class MotorRenderer implements BlockEntityRenderer<MotorBlockEntity, SingleKineticRenderState> {
    public MotorRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public SingleKineticRenderState createRenderState() {
        return new SingleKineticRenderState();
    }

    @Override
    public void extractRenderState(MotorBlockEntity be, SingleKineticRenderState state, float partialTicks, Vec3 cameraPosition,
                                   @Nullable CrumblingOverlay breakProgress) {
        Level level = SmartBlockEntityRenderer.extractBase(be, state, breakProgress);
        Direction facing = state.blockState.getValue(BlockStateProperties.FACING);
        state.model = CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, state.blockState, facing)
                .cardinalLighting(level).light(state.lightCoords).color(KineticBlockEntityRenderer.getTintColor(be)).extractRenderState();
        state.angle = KineticBlockEntityRenderer.getRotateAngleWithoutBeOffset(facing.getAxis(), be, state, level);
    }

    @Override
    public void submit(SingleKineticRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState camera) {
        state.submit(matrices, queue);
    }
}
