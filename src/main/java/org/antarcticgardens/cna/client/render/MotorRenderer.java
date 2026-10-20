package org.antarcticgardens.cna.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.antarcticgardens.cna.content.motor.MotorBlockEntity;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import static org.antarcticgardens.cna.client.render.KineticRenderSupport.*;

/** The motor's half shaft when Flywheel is off. Was {@code HalfShaftRenderer}; same as Create Fly's creative motor. */
public class MotorRenderer implements BlockEntityRenderer<MotorBlockEntity, MotorRenderer.State> {
    public MotorRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(MotorBlockEntity be, State state, float partialTicks, Vec3 cameraPosition,
                                   @Nullable CrumblingOverlay breakProgress) {
        Level level = extractBase(be, state, breakProgress);
        Direction facing = state.blockState.getValue(BlockStateProperties.FACING);
        state.shaft = new Part(CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, state.blockState, facing),
                RenderType.solid(), state.lightCoords, tintColor(be));
        state.angle = rotationWithoutBeOffset(facing.getAxis(), be, state, level);
    }

    @Override
    public void submit(State state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState camera) {
        matrices.rotateAround(state.angle, 0.5f, 0.5f, 0.5f);
        state.shaft.submit(matrices, queue);
    }

    public static class State extends BlockEntityRenderState {
        public Part shaft;
        public Quaternionf angle;
    }
}
