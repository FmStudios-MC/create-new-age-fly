package org.antarcticgardens.cna.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zurrtum.create.client.catnip.animation.AnimationTickHolder;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityVisual;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.function.UnaryOperator;

/**
 * The render helpers Create Fly gained after 1.21.11 ({@code SmartBlockEntityRenderer.extractBase},
 * {@code getTintColor}, {@code getRotateAngleWithoutBeOffset}, {@code SuperByteBufferRenderState}),
 * rebuilt on what Create Fly offers on 1.21.11.
 */
public final class KineticRenderSupport {
    private KineticRenderSupport() {
    }

    /** Fills the base render state; returns the block entity's level. */
    public static @Nullable Level extractBase(BlockEntity be, BlockEntityRenderState state, @Nullable CrumblingOverlay breakProgress) {
        Level level = be.getLevel();
        state.blockPos = be.getBlockPos();
        state.blockState = be.getBlockState();
        state.blockEntityType = be.getType();
        state.lightCoords = level != null ? LevelRenderer.getLightColor(level, state.blockPos) : LightTexture.FULL_BRIGHT;
        state.breakProgress = breakProgress;
        return level;
    }

    /** White, or the stress/debug colour Create tints kinetic parts with. */
    public static int tintColor(KineticBlockEntity be) {
        return KineticBlockEntityRenderer.getColor(be).getRGB();
    }

    /** The shaft's rotation about {@code axis}, without the block entity's own angle offset. */
    public static Quaternionf rotationWithoutBeOffset(Direction.Axis axis, KineticBlockEntity be, BlockEntityRenderState state,
                                                      @Nullable Level level) {
        float time = AnimationTickHolder.getRenderTime(level);
        float offset = KineticBlockEntityVisual.rotationOffset(state.blockState, axis, state.blockPos);
        float angle = ((time * be.getSpeed() * 3f / 10 + offset) % 360) / 180 * (float) Math.PI;
        return rotation(axis, angle);
    }

    public static Quaternionf rotation(Direction.Axis axis, float radians) {
        return new Quaternionf().setAngleAxis(radians,
                axis == Direction.Axis.X ? 1 : 0, axis == Direction.Axis.Y ? 1 : 0, axis == Direction.Axis.Z ? 1 : 0);
    }

    /**
     * A cached buffer to draw on submit. Cached buffers are shared between block entities, and their
     * transforms are reset by every {@code renderInto}, so the transform, light and colour are applied
     * when the geometry is rendered, not when the state is extracted.
     */
    public record Part(SuperByteBuffer buffer, RenderType renderType, int light, int color,
                       @Nullable UnaryOperator<SuperByteBuffer> transform) implements SubmitNodeCollector.CustomGeometryRenderer {
        public Part(SuperByteBuffer buffer, RenderType renderType, int light, int color) {
            this(buffer, renderType, light, color, null);
        }

        public void submit(PoseStack matrices, SubmitNodeCollector queue) {
            queue.submitCustomGeometry(matrices, renderType, this);
        }

        @Override
        public void render(PoseStack.Pose pose, VertexConsumer consumer) {
            SuperByteBuffer transformed = transform != null ? transform.apply(buffer) : buffer;
            transformed.light(light).color(color).renderInto(pose, consumer);
        }
    }
}
