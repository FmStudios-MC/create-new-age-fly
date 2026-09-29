package org.antarcticgardens.cna.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.catnip.math.AngleHelper;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBufferRenderState;
import com.zurrtum.create.client.flywheel.api.visualization.VisualizationManager;
import com.zurrtum.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.antarcticgardens.cna.content.heat.stirling.StirlingEngineBlockEntity;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import static com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer.*;

/**
 * The miniature flywheel, and the shaft when Flywheel is off (the visual draws it otherwise).
 * <p>
 * Upstream only drew the flywheel when visualization was supported, while that renderer was also the
 * one skipped when visualization ran, so the flywheel showed in neither case. It is drawn always here.
 */
public class StirlingEngineRenderer implements BlockEntityRenderer<StirlingEngineBlockEntity, StirlingEngineRenderer.State> {
    public StirlingEngineRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(StirlingEngineBlockEntity be, State state, float partialTicks, Vec3 cameraPosition,
                                   @Nullable CrumblingOverlay breakProgress) {
        Level level = SmartBlockEntityRenderer.extractBase(be, state, breakProgress);
        Direction.Axis axis = state.blockState.getValue(BlockStateProperties.AXIS);
        int color = getTintColor(be);

        state.shaft = null;
        if (!VisualizationManager.supportsVisualization(level)) {
            state.shaft = CachedBuffers.block(KINETIC_BLOCK, shaft(axis)).cardinalLighting(level).light(state.lightCoords)
                    .color(color).extractRenderState();
            state.shaftAngle = getRotateAngleWithoutBeOffset(axis, be, state, level);
        }

        BlockState wheelState = AllBlocks.FLYWHEEL.defaultBlockState().setValue(BlockStateProperties.AXIS, axis);
        float speed = be.visualSpeed.getValue(partialTicks) * 3 / 10f;
        float angle = be.angle + speed * partialTicks;
        state.wheelAngle = new Quaternionf().setAngleAxis(AngleHelper.rad(angle),
                axis == Direction.Axis.X ? 1 : 0, axis == Direction.Axis.Y ? 1 : 0, axis == Direction.Axis.Z ? 1 : 0);
        state.wheel = CachedBuffers.block(wheelState).center().scale(0.2f, 0.2f, 0.2f).uncenter()
                .cardinalLighting(level).light(state.lightCoords).extractRenderState();
    }

    @Override
    public void submit(State state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState camera) {
        if (state.shaft != null) {
            matrices.pushPose();
            if (state.shaftAngle != null)
                matrices.rotateAround(state.shaftAngle, 0.5f, 0.5f, 0.5f);
            state.shaft.submit(matrices, queue);
            matrices.popPose();
        }
        matrices.rotateAround(state.wheelAngle, 0.5f, 0.5f, 0.5f);
        state.wheel.submit(matrices, queue);
    }

    public static class State extends BlockEntityRenderState {
        public @Nullable SuperByteBufferRenderState shaft;
        public @Nullable Quaternionf shaftAngle;
        public SuperByteBufferRenderState wheel;
        public Quaternionf wheelAngle;
    }
}
