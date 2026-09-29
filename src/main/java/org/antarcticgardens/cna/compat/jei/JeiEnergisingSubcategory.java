package org.antarcticgardens.cna.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.antarcticgardens.cna.compat.RecipeViewerEnergiserRenderer;
import org.antarcticgardens.cna.content.energising.recipe.EnergisingRecipe;
import org.antarcticgardens.cna.util.StringFormatUtil;

public class JeiEnergisingSubcategory {
    public static final int WIDTH = 25;
    
    private final AnimatedKinetics energiser = new AnimatedKinetics() {
        private final RecipeViewerEnergiserRenderer renderer = new RecipeViewerEnergiserRenderer(this::blockElement);

        @Override
        public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
            renderer.draw(graphics, xOffset, yOffset);
        }
    };

    public void draw(SequencedRecipe<?> recipe, GuiGraphics graphics, double mouseX, double mouseY, int index) {
        PoseStack ms = graphics.pose();
        energiser.offset = index;
        ms.pushPose();
        ms.translate(-7, 50, 0);
        ms.scale(.75f, .75f, .75f);
        energiser.draw(graphics, WIDTH / 2, 0);
        ms.popPose();
        graphics.drawString(Minecraft.getInstance().font, Component.literal(StringFormatUtil.formatLong(((EnergisingRecipe)recipe.getAsAssemblyRecipe()).getEnergyNeeded()) + " ⚡"), 0, 20, 0x1166ff, false);
    }
}
