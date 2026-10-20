package org.antarcticgardens.cna.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.catnip.math.AngleHelper;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.antarcticgardens.cna.content.heat.stirling.StirlingEngineBlockEntity;

/**
 * The miniature flywheel, and the shaft when Flywheel is off (the visual draws it otherwise).
 * <p>
 * Upstream only drew the flywheel when visualization was supported, while that renderer was also the
 * one skipped when visualization ran, so the flywheel showed in neither case. It is drawn always here.
 */
public class StirlingEngineRenderer extends KineticBlockEntityRenderer<StirlingEngineBlockEntity> {
    public StirlingEngineRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(StirlingEngineBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

        Direction.Axis axis = be.getBlockState().getValue(BlockStateProperties.AXIS);
        BlockState wheelState = AllBlocks.FLYWHEEL.defaultBlockState().setValue(BlockStateProperties.AXIS, axis);
        float speed = be.visualSpeed.getValue(partialTicks) * 3 / 10f;
        float angle = be.angle + speed * partialTicks;

        SuperByteBuffer wheel = CachedBuffers.block(wheelState).center().scale(0.2f, 0.2f, 0.2f).uncenter();
        kineticRotationTransform(wheel, be, axis, AngleHelper.rad(angle), light);
        wheel.renderInto(ms, buffer.getBuffer(RenderType.solid()));
    }

    @Override
    protected BlockState getRenderedBlockState(StirlingEngineBlockEntity be) {
        return shaft(be.getBlockState().getValue(BlockStateProperties.AXIS));
    }
}
