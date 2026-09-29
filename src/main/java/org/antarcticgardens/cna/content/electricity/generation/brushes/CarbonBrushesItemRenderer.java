package org.antarcticgardens.cna.content.electricity.generation.brushes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.antarcticgardens.cna.CNAPartialModels;
import org.antarcticgardens.cna.CreateNewAge;
import org.antarcticgardens.cna.rendering.ItemShaftRenderer;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class CarbonBrushesItemRenderer extends ItemShaftRenderer {
    protected CarbonBrushesItemRenderer(Vector3f offset, Quaternionf rotation) {
        super(offset, rotation);
    }

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, 
                          ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.render(stack, model, renderer, transformType, ms, buffer, light, overlay);

        ms.mulPose(rotation);
        ms.mulPose(Axis.YP.rotation(1.0f));
        ms.translate(offset.x, offset.y, offset.z);
        renderer.render(CNAPartialModels.COIL.get(), light);
    }

}
