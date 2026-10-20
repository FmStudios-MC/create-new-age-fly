package org.antarcticgardens.cna.client.compat.rei;

import com.zurrtum.create.client.compat.rei.category.SequencedAssemblyCategory;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.world.item.ItemStack;
import org.antarcticgardens.cna.CNABlocks;
import org.antarcticgardens.cna.CNARecipeTypes;
import org.antarcticgardens.cna.compat.rei.CNAReiCommonPlugin;

/** Registered through the {@code rei_client} entrypoint; see {@link CNAReiCommonPlugin}. */
public class CNAReiClientPlugin implements REIClientPlugin {
    private static final ItemStack ENERGISER = new ItemStack(CNABlocks.BASIC_ENERGISER);

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new EnergisingCategory(), config -> config.addWorkstations(
                EntryStacks.of(CNABlocks.BASIC_ENERGISER),
                EntryStacks.of(CNABlocks.ADVANCED_ENERGISER),
                EntryStacks.of(CNABlocks.REINFORCED_ENERGISER)));
        // An energising step inside a sequenced assembly: the energiser over the step's slot.
        SequencedAssemblyCategory.registerDraw(CNARecipeTypes.ENERGISING,
                (graphics, i, point, stack) -> graphics.renderItem(ENERGISER, point.x, point.y + 16));
    }
}
