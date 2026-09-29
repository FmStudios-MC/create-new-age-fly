package org.antarcticgardens.cna.client.compat.jei;

import com.zurrtum.create.client.compat.jei.category.SequencedAssemblyCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.common.Internal;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.antarcticgardens.cna.CNABlocks;
import org.antarcticgardens.cna.CNARecipeTypes;
import org.antarcticgardens.cna.CreateNewAge;
import org.antarcticgardens.cna.content.energising.recipe.EnergisingRecipe;

import java.util.Optional;

/**
 * Registered through the {@code jei_mod_plugin} entrypoint, which is how JEI finds plugins on Fabric.
 * Recipes come from the set JEI syncs to the client, as for Create Fly's own categories.
 */
public class CNAJeiPlugin implements IModPlugin {
    @SuppressWarnings("unchecked")
    public static final IRecipeType<RecipeHolder<EnergisingRecipe>> ENERGISING =
            (IRecipeType<RecipeHolder<EnergisingRecipe>>) (Object) IRecipeType.create(
                    Identifier.fromNamespaceAndPath(CreateNewAge.MOD_ID, "energising"), RecipeHolder.class);

    @Override
    public Identifier getPluginUid() {
        return Identifier.fromNamespaceAndPath(CreateNewAge.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new EnergisingCategory());
        SequencedAssemblyCategory.registerRenderer(CNARecipeTypes.ENERGISING, new EnergisingStepRenderer());
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(ENERGISING, EnergisingCategory.getRecipes(Internal.getClientSyncedRecipes()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(ENERGISING, CNABlocks.BASIC_ENERGISER, CNABlocks.ADVANCED_ENERGISER, CNABlocks.REINFORCED_ENERGISER);
    }

    /** An energising step inside a sequenced assembly: the energiser and the energy it takes. */
    static class EnergisingStepRenderer extends SequencedAssemblyCategory.SequencedRenderer<EnergisingRecipe> {
        private static final ItemStack ENERGISER = new ItemStack(CNABlocks.BASIC_ENERGISER);

        @Override
        public void render(GuiGraphicsExtractor graphics, int i, int x, int y, Optional<IRecipeSlotView> slot) {
            graphics.item(ENERGISER, x + 1, y + 16);
        }
    }
}
