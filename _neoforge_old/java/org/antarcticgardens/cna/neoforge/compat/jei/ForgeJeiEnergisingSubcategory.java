package org.antarcticgardens.cna.neoforge.compat.jei;

import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import net.minecraft.client.gui.GuiGraphics;
import org.antarcticgardens.cna.compat.jei.JeiEnergisingSubcategory;

public class ForgeJeiEnergisingSubcategory extends SequencedAssemblySubCategory {
    private final JeiEnergisingSubcategory subcategory = new JeiEnergisingSubcategory();
    
    public ForgeJeiEnergisingSubcategory() {
        super(JeiEnergisingSubcategory.WIDTH);
    }

    @Override
    public void draw(SequencedRecipe<?> recipe, GuiGraphics graphics, double mouseX, double mouseY, int index) {
        subcategory.draw(recipe, graphics, mouseX, mouseY, index);
    }
}
