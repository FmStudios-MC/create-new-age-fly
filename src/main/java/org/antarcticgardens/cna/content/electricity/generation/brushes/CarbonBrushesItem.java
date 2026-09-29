package org.antarcticgardens.cna.content.electricity.generation.brushes;

import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.antarcticgardens.cna.rendering.ItemShaftRenderer;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.Consumer;

@SuppressWarnings({"deprecation", "removal"})
public class CarbonBrushesItem extends BlockItem {
    public CarbonBrushesItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new CarbonBrushesItemRenderer(
                new Vector3f(0.0f), new Quaternionf())));
    }
}
