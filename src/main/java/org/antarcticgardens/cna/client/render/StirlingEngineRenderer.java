package org.antarcticgardens.cna.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.catnip.math.AngleHelper;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.flywheel.api.visualization.VisualizationManager;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.antarcticgardens.cna.content.heat.stirling.StirlingEngineBlockEntity;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import static com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer.KINETIC_BLOCK;
import static com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer.shaft;
import static org.antarcticgardens.cna.client.render.KineticRenderSupport.*;

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
        Level level = extractBase(be, state, breakProgress);
        Direction.Axis axis = state.blockState.getValue(BlockStateProperties.AXIS);
        int color = tintColor(be);

        state.shaft = null;
        if (!VisualizationManager.supportsVisualization(level)) {
            state.shaft = new Part(CachedBuffers.block(KINETIC_BLOCK, shaft(axis)), RenderTypes.solidMovingBlock(), state.lightCoords, color);
            state.shaftAngle = rotationWithoutBeOffset(axis, be, state, level);
        }

        BlockState wheelState = AllBlocks.FLYWHEEL.defaultBlockState().setValue(BlockStateProperties.AXIS, axis);
        float speed = be.visualSpeed.getValue(partialTicks) * 3 / 10f;
        float angle = be.angle + speed * partialTicks;
        state.wheelAngle = rotation(axis, AngleHelper.rad(angle));
        state.wheel = new Part(CachedBuffers.block(wheelState), RenderTypes.solidMovingBlock(), state.lightCoords, -1,
                wheel -> wheel.center().scale(0.2f, 0.2f, 0.2f).uncenter());
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
        public @Nullable Part shaft;
        public @Nullable Quaternionf shaftAngle;
        public Part wheel;
        public Quaternionf wheelAngle;
    }
}
