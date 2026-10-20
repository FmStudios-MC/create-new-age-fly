package org.antarcticgardens.cna.client.compat.rei;

import com.zurrtum.create.client.compat.rei.CreateCategory;
import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import com.zurrtum.create.content.processing.recipe.ProcessingOutput;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.antarcticgardens.cna.CNABlocks;
import org.antarcticgardens.cna.compat.rei.CNAReiCommonPlugin;
import org.antarcticgardens.cna.compat.rei.EnergisingDisplay;
import org.antarcticgardens.cna.util.StringFormatUtil;

import java.util.ArrayList;
import java.util.List;

/** Upstream's energising category (JEI there), on Create Fly's REI base class with the same layout. */
public class EnergisingCategory extends CreateCategory<EnergisingDisplay> {
    @Override
    public CategoryIdentifier<? extends EnergisingDisplay> getCategoryIdentifier() {
        return CNAReiCommonPlugin.ENERGISING;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("category.create_new_age.energising");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(CNABlocks.ADVANCED_ENERGISER);
    }

    @Override
    public void addWidgets(List<Widget> widgets, EnergisingDisplay display, Rectangle bounds) {
        int x = bounds.x + 5, y = bounds.y + 5;
        Point input = new Point(x + 4, y + 4);
        List<Point> outputs = new ArrayList<>();
        List<EntryIngredient> outputIngredients = new ArrayList<>();
        List<Point> chances = new ArrayList<>();
        List<EntryIngredient> chanceIngredients = new ArrayList<>();
        List<ProcessingOutput> results = display.outputs();
        for (int i = 0; i < results.size(); i++)
            addOutputData(results.get(i), x + 80 + (i % 5) * 19, y + 4 + (i / 5) * -19,
                    outputs, outputIngredients, chances, chanceIngredients);

        widgets.add(Widgets.createDrawableWidget((GuiGraphics graphics, int mouseX, int mouseY, float delta) -> {
            drawSlotBackground(graphics, outputs, input);
            drawChanceSlotBackground(graphics, chances);
            AllGuiTextures.JEI_ARROW.render(graphics, x + 31, y + 8);
            drawEnergy(graphics, display.energyNeeded(), x + 50, y + 18);
        }));
        widgets.add(createInputSlot(input).entries(display.input()));
        for (int i = 0; i < outputs.size(); i++)
            widgets.add(createOutputSlot(outputs.get(i)).entries(outputIngredients.get(i)));
        for (int i = 0; i < chances.size(); i++)
            widgets.add(createOutputSlot(chances.get(i)).entries(chanceIngredients.get(i)));
    }

    @Override
    public int getDisplayWidth(EnergisingDisplay display) {
        return 140;
    }

    @Override
    public int getDisplayHeight() {
        return 36;
    }

    /** Upstream drew this centred and without shadow, in 0x1166ff. */
    static void drawEnergy(GuiGraphics graphics, long energy, int centreX, int y) {
        Font font = Minecraft.getInstance().font;
        Component text = Component.literal(StringFormatUtil.formatLong(energy) + " ⚡");
        graphics.drawString(font, text, centreX - font.width(text) / 2, y, 0xFF1166FF, false);
    }
}
