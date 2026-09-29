package org.antarcticgardens.cna.content.heat.stirling;

import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.antarcticgardens.cna.rendering.ItemShaftRenderer;
import org.joml.Vector3f;
import org.openjdk.nashorn.internal.ir.annotations.Ignore;

import java.util.function.Consumer;

@SuppressWarnings({"deprecation", "removal"})
public class StirlingEngineItem extends BlockItem {
    public StirlingEngineItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new ItemShaftRenderer(
                new Vector3f(0.5f, 0.0f, 0.0f), Axis.XP.rotationDegrees(90.0f))));
    }
}
