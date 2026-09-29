package org.antarcticgardens.cna;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.antarcticgardens.cna.content.energising.recipe.EnergisingRecipe;

public class CNARecipeTypes {
    public static final RecipeType<EnergisingRecipe> ENERGISING = registerType("energising");
    public static final RecipeSerializer<EnergisingRecipe> ENERGISING_SERIALIZER = registerSerializer("energising", EnergisingRecipe.SERIALIZER);

    static void init() {
    }

    private static <T extends Recipe<?>> RecipeType<T> registerType(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(CreateNewAge.MOD_ID, name);
        return Registry.register(BuiltInRegistries.RECIPE_TYPE, id, new RecipeType<T>() {
            @Override
            public String toString() {
                return id.toString();
            }
        });
    }

    private static <T extends Recipe<?>> RecipeSerializer<T> registerSerializer(String name, RecipeSerializer<T> serializer) {
        return Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath(CreateNewAge.MOD_ID, name), serializer);
    }
}
