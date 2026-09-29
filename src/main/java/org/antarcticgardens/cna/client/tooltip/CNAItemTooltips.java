package org.antarcticgardens.cna.client.tooltip;

import com.zurrtum.create.client.foundation.item.TooltipModifier;
import com.zurrtum.create.client.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import org.antarcticgardens.cna.CreateNewAge;
import org.antarcticgardens.cna.config.CNAConfig;
import org.antarcticgardens.cna.content.electricity.generation.magnet.ImplementedMagnetBlock;
import org.antarcticgardens.cna.content.electricity.light.StreetLightBlock;
import org.antarcticgardens.cna.content.electricity.wire.ElectricWireItem;
import org.antarcticgardens.cna.content.energising.EnergiserBlock;
import org.antarcticgardens.cna.content.heat.plate.SolarHeatingPlateBlock;
import org.antarcticgardens.cna.content.motor.MotorBlock;
import org.antarcticgardens.cna.content.motor.extension.MotorExtensionBlock;
import org.antarcticgardens.cna.util.StringFormatUtil;

import java.util.List;

/**
 * Item tooltip lines. 26.2 removed {@code Block#appendHoverText}, and these build their lines with
 * Create's client-only {@code CreateLang}, so each moved here unchanged from its block or item and
 * is attached through Create Fly's {@link TooltipModifier} registry.
 */
public final class CNAItemTooltips {
    private CNAItemTooltips() {
    }

    public static void register() {
        for (Item item : BuiltInRegistries.ITEM) {
            if (!CreateNewAge.MOD_ID.equals(BuiltInRegistries.ITEM.getKey(item).getNamespace()))
                continue;
            TooltipModifier modifier = modifierFor(item);
            if (modifier != null)
                TooltipModifier.REGISTRY.register(item, modifier);
        }
    }

    private static TooltipModifier modifierFor(Item item) {
        if (item instanceof ElectricWireItem wire)
            return (tooltip, player) -> wire(tooltip, wire);
        if (!(item instanceof BlockItem blockItem))
            return null;
        return switch (blockItem.getBlock()) {
            case EnergiserBlock block -> (tooltip, player) -> energiser(tooltip, block);
            case ImplementedMagnetBlock block -> (tooltip, player) -> magnet(tooltip, block);
            case MotorBlock block -> (tooltip, player) -> motor(tooltip, block);
            case MotorExtensionBlock block -> (tooltip, player) -> motorExtension(tooltip, block);
            case SolarHeatingPlateBlock block -> (tooltip, player) -> solarHeatingPlate(tooltip, block);
            case StreetLightBlock block -> (tooltip, player) -> streetLight(tooltip);
            default -> null;
        };
    }

    private static void wire(List<Component> tooltip, ElectricWireItem item) {
        tooltip.add(CreateLang.translate("tooltip.create_new_age.transfers").style(ChatFormatting.GRAY)
                .component());
        tooltip.add(CreateLang.text(" ").translate("tooltip.create_new_age.energy_per_tick",
                String.format("%,d", item.getWireType().getConductivity())).style(ChatFormatting.AQUA).component());
    }

    private static void energiser(List<Component> tooltip, EnergiserBlock block) {
        tooltip.add(CreateLang.translate("tooltip.create_new_age.speed").style(ChatFormatting.GRAY).component());
        tooltip.add(CreateLang.text(" ").translate("tooltip.create_new_age.energy_per_tick",
                        StringFormatUtil.formatLong(EnergiserBlock.getStrength(block.getTier()))).style(ChatFormatting.AQUA)
                .add(CreateLang.text(" ").translate("tooltip.create_new_age.per_rpm", 10).style(ChatFormatting.GRAY)).component());
        tooltip.add(CreateLang.translate("tooltip.create_new_age.stores").style(ChatFormatting.GRAY).component());
        tooltip.add(CreateLang.text(" ").translate("tooltip.create_new_age.energy",
                StringFormatUtil.formatLong(EnergiserBlock.getCapacity(block.getTier()))).style(ChatFormatting.AQUA).component());
    }

    private static void magnet(List<Component> tooltip, ImplementedMagnetBlock block) {
        tooltip.add(CreateLang.translate("tooltip.create_new_age.magnetic_force").style(ChatFormatting.GRAY).component());
        tooltip.add(CreateLang.text(" " + (int) block.getStrength()).style(ChatFormatting.AQUA)
                .component());
    }

    private static void motor(List<Component> tooltip, MotorBlock block) {
        tooltip.add(CreateLang.translate("tooltip.create_new_age.generates").style(ChatFormatting.GRAY)
                .component());
        tooltip.add(CreateLang.text(" ").add(CreateLang.number(block.getVariant().getStress() * CNAConfig.getServer().motorSUMultiplier.get()).text(" ")
                .translate("generic.unit.stress").style(ChatFormatting.AQUA)).component());

        tooltip.add(CreateLang.translate("tooltip.create_new_age.stores").style(ChatFormatting.GRAY)
                .component());
        tooltip.add(CreateLang.text(" ").translate("tooltip.create_new_age.energy",
                StringFormatUtil.formatLong(block.getVariant().getMaxCapacity())).style(ChatFormatting.AQUA).component());

        tooltip.add(CreateLang.translate("tooltip.create_new_age.max_speed").style(ChatFormatting.GRAY)
                .component());
        tooltip.add(CreateLang.text(" ").translate("tooltip.create_new_age.rpm", block.getVariant().getSpeed()).style(ChatFormatting.AQUA).component());
    }

    private static void motorExtension(List<Component> tooltip, MotorExtensionBlock block) {
        tooltip.add(Component.translatable("tooltip.create_new_age.motor_extension").withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(CreateLang.translate("tooltip.create_new_age.stress_limit_multiplier").style(ChatFormatting.GRAY)
                .component());
        tooltip.add(CreateLang.text(" ").add(CreateLang.number((int) (block.getVariant().getMultiplier() * 100)).text("%").style(ChatFormatting.AQUA)).component());

        tooltip.add(CreateLang.translate("tooltip.create_new_age.additional_capacity").style(ChatFormatting.GRAY)
                .component());
        tooltip.add(CreateLang.text(" ").add(CreateLang.number(block.getVariant().getExtraCapacity()).text("⚡").style(ChatFormatting.AQUA)).component());
    }

    private static void solarHeatingPlate(List<Component> tooltip, SolarHeatingPlateBlock block) {
        tooltip.add(CreateLang.translate("tooltip.create_new_age.generates").style(ChatFormatting.GRAY)
                .component());
        tooltip.add(CreateLang.text(" ").translate("tooltip.create_new_age.temperature.ps",
                block.getHeatStrength() * CNAConfig.getServer().solarPanelHeatMultiplier.get()).style(ChatFormatting.AQUA).component());
    }

    private static void streetLight(List<Component> tooltip) {
        tooltip.add(CreateLang.translate("tooltip.create_new_age.speed").style(ChatFormatting.GRAY).component());
        tooltip.add(CreateLang.text(" ").translate("tooltip.create_new_age.energy_per_tick",
                        StringFormatUtil.formatLong(CNAConfig.getServer().streetLightLevelExtraction.get())).style(ChatFormatting.AQUA)
                .add(CreateLang.text(" ").translate("tooltip.create_new_age.per_light_level").style(ChatFormatting.GRAY)).component());
        tooltip.add(CreateLang.translate("tooltip.create_new_age.stores").style(ChatFormatting.GRAY).component());
        tooltip.add(CreateLang.text(" ").translate("tooltip.create_new_age.energy",
                StringFormatUtil.formatLong(CNAConfig.getServer().streetLightCapacity.get())).style(ChatFormatting.AQUA).component());
    }
}
