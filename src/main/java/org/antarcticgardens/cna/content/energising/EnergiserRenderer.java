package org.antarcticgardens.cna.content.energising;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer;
import com.zurrtum.create.client.content.kinetics.base.ShaftRenderer;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.Blocks;

public class EnergiserRenderer extends ShaftRenderer<EnergiserBlockEntity> {
    public EnergiserRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(EnergiserBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
        if (blockEntity.size > 0f && blockEntity.getLevel() != null) {
            var consumer = buffer.getBuffer(RenderType.lightning());
            float scalar = (1 - blockEntity.size * 0.12f) * 0.5f;
            var buf = CachedBuffers.block(Blocks.WHITE_CONCRETE.defaultBlockState());

            buf.color(100, 150, 200, 200)
                    .translate(scalar, -1.2, scalar)
                    .scale(blockEntity.size * 0.12f, 1.3f, blockEntity.size * 0.12f)
                    .renderInto(poseStack, consumer);

        }
        super.renderSafe(blockEntity, partialTicks, poseStack, buffer, light, overlay);
    }
}
