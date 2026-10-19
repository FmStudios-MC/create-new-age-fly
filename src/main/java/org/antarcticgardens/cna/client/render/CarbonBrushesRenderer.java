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
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.antarcticgardens.cna.content.electricity.generation.brushes.CarbonBrushesBlockEntity;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import static com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer.KINETIC_BLOCK;
import static com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer.shaft;
import static org.antarcticgardens.cna.client.render.KineticRenderSupport.*;

/** The rotating coil, and the shaft when Flywheel is off (the visual draws it otherwise). */
public class CarbonBrushesRenderer implements BlockEntityRenderer<CarbonBrushesBlockEntity, CarbonBrushesRenderer.State> {
    public CarbonBrushesRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(CarbonBrushesBlockEntity be, State state, float partialTicks, Vec3 cameraPosition,
                                   @Nullable CrumblingOverlay breakProgress) {
        Level level = extractBase(be, state, breakProgress);
        Direction.Axis axis = ((IRotate) state.blockState.getBlock()).getRotationAxis(state.blockState);
        int color = tintColor(be);
        state.angle = rotationWithoutBeOffset(axis, be, state, level);

        state.shaft = VisualizationManager.supportsVisualization(level) ? null
                : new Part(CachedBuffers.block(KINETIC_BLOCK, shaft(axis)), RenderTypes.solidMovingBlock(), state.lightCoords, color);

        // The coil model stands along Y; turn it onto the shaft's axis before spinning it with the shaft.
        Direction facing = state.blockState.getValue(BlockStateProperties.FACING);
        state.coil = new Part(CachedBuffers.partial(CNAPartialModels.COIL, state.blockState), RenderTypes.cutoutMovingBlock(),
                state.lightCoords, color, coil -> {
            coil.center();
            switch (facing.getAxis()) {
                case X -> coil.rotateZDegrees(90);
                case Z -> coil.rotateXDegrees(90);
                default -> {
                }
            }
            return coil.uncenter();
        });
    }

    @Override
    public void submit(State state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState camera) {
        if (state.angle != null)
            matrices.rotateAround(state.angle, 0.5f, 0.5f, 0.5f);
        if (state.shaft != null)
            state.shaft.submit(matrices, queue);
        state.coil.submit(matrices, queue);
    }

    public static class State extends BlockEntityRenderState {
        public @Nullable Part shaft;
        public Part coil;
        public @Nullable Quaternionf angle;
    }
}
