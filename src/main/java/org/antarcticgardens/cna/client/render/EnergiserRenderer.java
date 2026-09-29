package org.antarcticgardens.cna.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBufferRenderState;
import com.zurrtum.create.client.flywheel.api.visualization.VisualizationManager;
import com.zurrtum.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.antarcticgardens.cna.content.energising.EnergiserBlockEntity;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import static com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer.*;

/**
 * The energiser's beam, and its shaft when Flywheel is off (the visual draws it otherwise). Upstream
 * extended Create's shaft renderer, which skipped the shaft itself under Flywheel the same way.
 */
public class EnergiserRenderer implements BlockEntityRenderer<EnergiserBlockEntity, EnergiserRenderer.State> {
    public EnergiserRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(EnergiserBlockEntity be, State state, float partialTicks, Vec3 cameraPosition,
                                   @Nullable CrumblingOverlay breakProgress) {
        Level level = SmartBlockEntityRenderer.extractBase(be, state, breakProgress);

        state.shaft = null;
        if (!VisualizationManager.supportsVisualization(level)) {
            Direction.Axis axis = getRotationAxisOf(state.blockState);
            state.shaft = CachedBuffers.block(KINETIC_BLOCK, shaft(axis)).cardinalLighting(level).light(state.lightCoords)
                    .color(getTintColor(be)).extractRenderState();
            state.angle = getRotateAngleWithoutBeOffset(axis, be, state, level);
        }

        state.beam = null;
        if (be.size > 0f) {
            float scalar = (1 - be.size * 0.12f) * 0.5f;
            state.beam = CachedBuffers.block(Blocks.CONCRETE.pick(DyeColor.WHITE).defaultBlockState())
                    .color(100, 150, 200, 200)
                    .translate(scalar, -1.2f, scalar)
                    .scale(be.size * 0.12f, 1.3f, be.size * 0.12f)
                    .extractRenderState();
        }
    }

    @Override
    public void submit(State state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState camera) {
        if (state.beam != null)
            state.beam.submit(RenderTypes.lightning(), matrices, queue);
        if (state.shaft != null) {
            if (state.angle != null)
                matrices.rotateAround(state.angle, 0.5f, 0.5f, 0.5f);
            state.shaft.submit(matrices, queue);
        }
    }

    public static class State extends BlockEntityRenderState {
        public @Nullable SuperByteBufferRenderState shaft;
        public @Nullable SuperByteBufferRenderState beam;
        public @Nullable Quaternionf angle;
    }
}
