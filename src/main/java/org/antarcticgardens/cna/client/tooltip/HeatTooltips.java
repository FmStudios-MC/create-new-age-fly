package org.antarcticgardens.cna.client.tooltip;

import com.zurrtum.create.client.catnip.lang.LangBuilder;
import com.zurrtum.create.client.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.antarcticgardens.cna.config.CNAConfig;
import org.antarcticgardens.cna.content.heat.HeatBlockEntity;
import org.antarcticgardens.cna.util.StringFormatUtil;

import java.util.List;

/** Was {@code HeatBlockEntity.addToolTips}; client-only since it builds goggle lines. */
public final class HeatTooltips {
    private HeatTooltips() {
    }

    public static <T extends BlockEntity & HeatBlockEntity> void addHeat(T self, List<Component> tooltip) {
        LangBuilder builder = CreateLang.translate("tooltip.create_new_age.temperature", StringFormatUtil.formatFloat(self.getHeat()));
        float max = self.maxHeat() * CNAConfig.getServer().overheatingMultiplier.get().floatValue();
        if (max < 0) {
            builder.style(ChatFormatting.AQUA);
        } else if (self.getHeat() >= max) {
            builder.style(ChatFormatting.DARK_RED);
        } else if (self.getHeat() >= max * 0.9) {
            builder.style(ChatFormatting.RED);
        } else if (self.getHeat() >= max * 0.75) {
            builder.style(ChatFormatting.GOLD);
        } else if (self.getHeat() >= max * 0.65) {
            builder.style(ChatFormatting.YELLOW);
        } else {
            builder.style(ChatFormatting.AQUA);
        }


        builder.add(CreateLang.text(" / ")
                .add(CreateLang.translate("tooltip.create_new_age.temperature", max > 0 ? StringFormatUtil.formatFloat(max) : "∞")).style(ChatFormatting.DARK_GRAY)
        );

        builder.forGoggles(tooltip, 1);

        float[] tiers = self.getHeatTiers();

        if (tiers == null)
            return;

        float mult = (float) self.getHeatTierMultiplier();
        float tierHeat = self.getTierHeat();
        for (int i = 0 ; i < tiers.length ; i++) {
            float tis = tiers[i] * mult;
            float next = Float.MAX_VALUE;
            if (tiers.length > i + 1) {
                next = tiers[i + 1] * mult;
            }
            if (tis <= tierHeat && next > tierHeat) {
                builder = CreateLang.text("> ").add(CreateLang.translate("tooltip.create_new_age.temperature", StringFormatUtil.formatFloat(tis)));
                builder.style(ChatFormatting.GRAY);
                builder.forGoggles(tooltip, 0);
            } else {
                builder = CreateLang.text("").add(CreateLang.translate("tooltip.create_new_age.temperature", StringFormatUtil.formatFloat(tis)));
                builder.style(ChatFormatting.DARK_GRAY);
                builder.forGoggles(tooltip, 2);
            }
        }

    }
}
