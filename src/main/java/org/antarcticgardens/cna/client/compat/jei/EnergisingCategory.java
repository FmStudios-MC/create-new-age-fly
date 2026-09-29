package org.antarcticgardens.cna.client.compat.jei;

import com.zurrtum.create.client.compat.jei.CreateCategory;
import com.zurrtum.create.client.compat.jei.renderer.IconRenderer;
import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import com.zurrtum.create.content.processing.recipe.ProcessingOutput;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import org.antarcticgardens.cna.CNABlocks;
import org.antarcticgardens.cna.CNARecipeTypes;
import org.antarcticgardens.cna.content.energising.recipe.EnergisingRecipe;
import org.antarcticgardens.cna.util.StringFormatUtil;

import java.util.List;

/** Upstream's energising category, on Create Fly's JEI base class. */
public class EnergisingCategory extends CreateCategory<RecipeHolder<EnergisingRecipe>> {
    public static List<RecipeHolder<EnergisingRecipe>> getRecipes(RecipeMap preparedRecipes) {
        return preparedRecipes.byType(CNARecipeTypes.ENERGISING).stream().toList();
    }

    @Override
    public IRecipeType<RecipeHolder<EnergisingRecipe>> getRecipeType() {
        return CNAJeiPlugin.ENERGISING;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("category.create_new_age.energising");
    }

    @Override
    public IDrawable getIcon() {
        return new IconRenderer(CNABlocks.ADVANCED_ENERGISER.asItem());
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<EnergisingRecipe> entry, IFocusGroup focuses) {
        EnergisingRecipe recipe = entry.value();
        builder.addInputSlot(4, 4).setBackground(SLOT, -1, -1).add(recipe.ingredient());
        List<ProcessingOutput> results = recipe.results();
        for (int i = 0; i < results.size(); i++)
            addChanceSlot(builder, 80 + (i % 5) * 19, 4 + (i / 5) * -19, results.get(i));
    }

    @Override
    public void draw(RecipeHolder<EnergisingRecipe> entry, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor graphics,
                     double mouseX, double mouseY) {
        AllGuiTextures.JEI_ARROW.render(graphics, 31, 8);
        drawEnergy(graphics, entry.value().getEnergyNeeded(), 50, 18);
    }

    /** Upstream drew this centred and without shadow, in 0x1166ff. */
    static void drawEnergy(GuiGraphicsExtractor graphics, long energy, int centreX, int y) {
        Font font = Minecraft.getInstance().font;
        Component text = Component.literal(StringFormatUtil.formatLong(energy) + " ⚡");
        graphics.text(font, text, centreX - font.width(text) / 2, y, 0xFF1166FF, false);
    }
}
