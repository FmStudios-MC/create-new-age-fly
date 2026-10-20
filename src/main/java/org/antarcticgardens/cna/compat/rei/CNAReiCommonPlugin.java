package org.antarcticgardens.cna.compat.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.plugins.REICommonPlugin;
import me.shedaniel.rei.api.common.registry.display.ServerDisplayRegistry;
import org.antarcticgardens.cna.CNARecipeTypes;
import org.antarcticgardens.cna.CreateNewAge;
import org.antarcticgardens.cna.content.energising.recipe.EnergisingRecipe;

/**
 * Registered through the {@code rei_common} entrypoint. JEI on Fabric 1.21.8 receives no recipes from
 * the server (and Create Fly 1.21.8 only supports REI), so the energising category lives in REI here.
 * Recipes stay on the server; REI turns them into displays there and syncs those to clients.
 */
public class CNAReiCommonPlugin implements REICommonPlugin {
    public static final CategoryIdentifier<EnergisingDisplay> ENERGISING = CategoryIdentifier.of(CreateNewAge.MOD_ID, "energising");

    @Override
    public void registerDisplays(ServerDisplayRegistry registry) {
        registry.beginRecipeFiller(EnergisingRecipe.class).filterType(CNARecipeTypes.ENERGISING).fill(EnergisingDisplay::new);
    }

    @Override
    public void registerDisplaySerializer(DisplaySerializerRegistry registry) {
        registry.register(ENERGISING.getIdentifier(), EnergisingDisplay.SERIALIZER);
    }
}
