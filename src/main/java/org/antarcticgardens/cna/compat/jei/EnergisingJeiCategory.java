package org.antarcticgardens.cna.compat.jei;

import com.simibubi.create.compat.jei.ItemIcon;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.zurrtum.create.content.processing.recipe.ProcessingOutput;
import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.antarcticgardens.cna.CNABlocks;
import org.antarcticgardens.cna.compat.RenderingUtil;
import org.antarcticgardens.cna.content.energising.recipe.EnergisingRecipe;
import org.antarcticgardens.cna.util.StringFormatUtil;

import java.util.Arrays;
import java.util.Collections;

import static com.simibubi.create.compat.jei.category.CreateRecipeCategory.getRenderedSlot;

public class EnergisingJeiCategory extends CreateRecipeCategory<EnergisingRecipe> {
    public EnergisingJeiCategory(Info<EnergisingRecipe> info) {
        super(info);
    }

    @Override
    public Component getTitle() {
        return Component.translatable("category.create_new_age.energising");
    }

    @Override
    public int getWidth() {
        return 130;
    }

    @Override
    public int getHeight() {
        return 25;
    }

    @Override
    public IDrawable getIcon() {
        return new ItemIcon(CNABlocks.ADVANCED_ENERGISER::asStack);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, EnergisingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 4, 4)
                .setBackground(getRenderedSlot(), -1, -1)
                .addItemStacks(Arrays.asList(recipe.getIngredients().get(0).getItems()));

        int i = 0;
        for (ProcessingOutput itemStacks : recipe.getRollableResults()) {
            int xPos = 80 + (i % 5) * 19;
            int yPos = 4 + (i / 5) * -19;

            builder
                    .addSlot(RecipeIngredientRole.OUTPUT, xPos, yPos)
                    .setBackground(getRenderedSlot(), -1, -1)
                    .addItemStacks(Collections.singletonList(itemStacks.getStack()));
            i++;
        }
    }

    @Override
    public void draw(EnergisingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        AllGuiTextures.JEI_ARROW.render(guiGraphics, 31, 8);
        RenderingUtil.drawCenteredStringWithoutShadow(guiGraphics, Minecraft.getInstance().font, Component.literal(StringFormatUtil.formatLong(recipe.getEnergyNeeded()) + " ⚡"), 50, 18, 0x1166ff);
    }
}
