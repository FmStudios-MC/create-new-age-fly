package org.antarcticgardens.cna.compat.jei;

import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import org.antarcticgardens.cna.CNARecipeTypes;
import org.antarcticgardens.cna.CreateNewAge;
import org.antarcticgardens.cna.CNABlocks;
import org.antarcticgardens.cna.content.energising.recipe.EnergisingRecipe;


@JeiPlugin
public class CNAJeiPlugin implements IModPlugin {
    public static CreateRecipeCategory<?> energisingType;

    @Override
    public Identifier getPluginUid() {
        return Identifier.fromNamespaceAndPath(CreateNewAge.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        energisingType = builder(EnergisingRecipe.class)
                .addTypedRecipes(CNARecipeTypes.ENERGISING)
                .catalyst(CNABlocks.BASIC_ENERGISER::get)
                .catalyst(CNABlocks.ADVANCED_ENERGISER::get)
                .catalyst(CNABlocks.REINFORCED_ENERGISER::get)
                .itemIcon(CNABlocks.BASIC_ENERGISER.get())
                .build(Identifier.fromNamespaceAndPath(CreateNewAge.MOD_ID, "energising"), EnergisingJeiCategory::new);
        registration.addRecipeCategories(
                energisingType
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        energisingType.registerRecipes(registration);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        energisingType.registerCatalysts(registration);
    }

    private <T extends Recipe<? extends RecipeInput>> CategoryBuilder<T> builder(Class<T> recipeClass) {
        return new CategoryBuilder<>(recipeClass);
    }

    private class CategoryBuilder<T extends Recipe<?>> extends CreateRecipeCategory.Builder<T> {
        public CategoryBuilder(Class<? extends T> recipeClass) {
            super(recipeClass);
        }

        @Override
        public CreateRecipeCategory<T> build(Identifier id, CreateRecipeCategory.Factory<T> factory) {
            CreateRecipeCategory<T> category = super.build(id, factory);
            return category;
        }
    }
}
