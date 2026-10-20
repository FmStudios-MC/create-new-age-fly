package org.antarcticgardens.cna.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.flywheel.api.visualization.VisualizationManager;
import com.zurrtum.create.content.kinetics.base.IRotate;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.antarcticgardens.cna.content.energising.EnergiserBlockEntity;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import static com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer.KINETIC_BLOCK;
import static com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer.shaft;
import static org.antarcticgardens.cna.client.render.KineticRenderSupport.*;

/**
 * The energiser's beam, and its shaft when Flywheel is off (the visual draws it otherwise). Upstream
 * extended Create's shaft renderer, which skipped the shaft itself under Flywheel the same way.
 */
public class EnergiserRenderer implements BlockEntityRenderer<EnergiserBlockEntity, EnergiserRenderer.State> {
    // upstream's color(100, 150, 200, 200) as ARGB
    private static final int BEAM_COLOR = 200 << 24 | 100 << 16 | 150 << 8 | 200;

    public EnergiserRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(EnergiserBlockEntity be, State state, float partialTicks, Vec3 cameraPosition,
                                   @Nullable CrumblingOverlay breakProgress) {
        Level level = extractBase(be, state, breakProgress);

        state.shaft = null;
        if (!VisualizationManager.supportsVisualization(level)) {
            Direction.Axis axis = ((IRotate) state.blockState.getBlock()).getRotationAxis(state.blockState);
            state.shaft = new Part(CachedBuffers.block(KINETIC_BLOCK, shaft(axis)), RenderType.solid(),
                    state.lightCoords, tintColor(be));
            state.angle = rotationWithoutBeOffset(axis, be, state, level);
        }

        state.beam = null;
        if (be.size > 0f) {
            float scalar = (1 - be.size * 0.12f) * 0.5f;
            float width = be.size * 0.12f;
            state.beam = new Part(CachedBuffers.block(Blocks.WHITE_CONCRETE.defaultBlockState()), RenderType.lightning(),
                    state.lightCoords, BEAM_COLOR,
                    buffer -> buffer.translate(scalar, -1.2f, scalar).scale(width, 1.3f, width));
        }
    }

    @Override
    public void submit(State state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState camera) {
        if (state.beam != null)
            state.beam.submit(matrices, queue);
        if (state.shaft != null) {
            if (state.angle != null)
                matrices.rotateAround(state.angle, 0.5f, 0.5f, 0.5f);
            state.shaft.submit(matrices, queue);
        }
    }

    public static class State extends BlockEntityRenderState {
        public @Nullable Part shaft;
        public @Nullable Part beam;
        public @Nullable Quaternionf angle;
    }
}
