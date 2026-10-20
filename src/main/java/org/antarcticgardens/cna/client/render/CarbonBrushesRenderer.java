package org.antarcticgardens.cna.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import com.zurrtum.create.client.content.kinetics.base.ShaftRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.antarcticgardens.cna.content.electricity.generation.brushes.CarbonBrushesBlockEntity;

/** The rotating coil, and the shaft when Flywheel is off (the visual draws it otherwise). */
public class CarbonBrushesRenderer extends ShaftRenderer<CarbonBrushesBlockEntity> {
    public CarbonBrushesRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(CarbonBrushesBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

        BlockState state = be.getBlockState();
        Direction facing = state.getValue(BlockStateProperties.FACING);

        SuperByteBuffer coil = CachedBuffers.partial(CNAPartialModels.COIL, state);
        standardKineticRotationTransform(coil, be, light);
        // The coil model stands along Y; turn it onto the shaft's axis.
        coil.center();
        switch (facing.getAxis()) {
            case X -> coil.rotateZDegrees(90);
            case Z -> coil.rotateXDegrees(90);
            default -> {
            }
        }
        coil.uncenter();
        coil.renderInto(ms, buffer.getBuffer(RenderType.cutout()));
    }
}
