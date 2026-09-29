package org.antarcticgardens.cna.content.electricity.generation.magnet;

import com.zurrtum.create.client.foundation.utility.CreateLang;
import java.util.function.Function;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ImplementedMagnetBlock extends Block implements IMagneticBlock {
    private final int strength;

    public ImplementedMagnetBlock(Properties properties, int strength) {
        super(properties.strength(5.0F, 7.0F));
        this.strength = strength;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(CreateLang.translate("tooltip.create_new_age.magnetic_force").style(ChatFormatting.GRAY).component());
        tooltipComponents.add(CreateLang.text(" " + strength).style(ChatFormatting.AQUA)
                .component());
    }

    @Override
    public float getStrength() {
        return strength;
    }

    public static Function<Properties, ImplementedMagnetBlock> simple(int level) {
        return (p) -> new ImplementedMagnetBlock(p, level);
    }
}
