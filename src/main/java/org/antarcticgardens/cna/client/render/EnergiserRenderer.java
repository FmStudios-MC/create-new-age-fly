package org.antarcticgardens.cna.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.content.kinetics.base.ShaftRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.Blocks;
import org.antarcticgardens.cna.content.energising.EnergiserBlockEntity;

/**
 * The energiser's beam, and its shaft when Flywheel is off (the shaft renderer skips it under
 * Flywheel, whose visual draws it). Upstream's renderer.
 */
public class EnergiserRenderer extends ShaftRenderer<EnergiserBlockEntity> {
    public EnergiserRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(EnergiserBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (be.size > 0f) {
            float scalar = (1 - be.size * 0.12f) * 0.5f;
            CachedBuffers.block(Blocks.WHITE_CONCRETE.defaultBlockState())
                    .color(100, 150, 200, 200)
                    .translate(scalar, -1.2f, scalar)
                    .scale(be.size * 0.12f, 1.3f, be.size * 0.12f)
                    .renderInto(ms, buffer.getBuffer(RenderType.lightning()));
        }
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
    }
}
