package org.antarcticgardens.cna.content.electricity.generation.brushes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer;
import com.zurrtum.create.client.content.kinetics.base.ShaftRenderer;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.antarcticgardens.cna.CNAPartialModels;

public class CarbonBrushesRenderer extends ShaftRenderer<CarbonBrushesBlockEntity> {
    public CarbonBrushesRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(CarbonBrushesBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

        VertexConsumer consumer = buffer.getBuffer(RenderType.solid());
        BlockState state = be.getBlockState();
        Direction dir = state.getValue(BlockStateProperties.FACING);

        SuperByteBuffer coil = CachedBuffers.partial(CNAPartialModels.COIL, state);
        KineticBlockEntityRenderer.standardKineticRotationTransform(coil, be, light);
        rotateToAxis(coil, dir.getAxis());
        coil.renderInto(ms, consumer);
    }
    
    private void rotateToAxis(SuperByteBuffer buffer, Direction.Axis axis) {
        buffer.center();
        
        switch (axis) {
            case X -> buffer.rotateDegrees(90, Direction.Axis.Z);
            case Z -> buffer.rotateDegrees(90, Direction.Axis.X);
        }
        
        buffer.uncenter();
    }
}
