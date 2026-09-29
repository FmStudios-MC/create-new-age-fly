package org.antarcticgardens.cna;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import com.zurrtum.create.client.catnip.lang.Lang;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.crafting.*;
import org.antarcticgardens.cna.content.energising.recipe.EnergisingRecipe;
import org.antarcticgardens.cna.content.energising.recipe.EnergisingRecipeParams;
import org.antarcticgardens.cna.platform.PlatformRegistrar;

import java.util.function.Supplier;

public enum CNARecipeTypes implements IRecipeTypeInfo, StringRepresentable {
    ENERGISING(EnergisingRecipe::new);

    public final Identifier id;
    private final RecipeSerializer<?> serializerObject;
    private final RecipeType<?> type;

    private final PlatformRegistrar register = CreateNewAge.getInstance().getPlatform().getRegistrar();

    CNARecipeTypes(Supplier<RecipeSerializer<?>> serializerSupplier) {
        String name = Lang.asId(name());
        id = Identifier.fromNamespaceAndPath(CreateNewAge.MOD_ID, name);
        serializerObject = serializerSupplier.get();
        type = register.registerRecipe(name, serializerObject);
    }

    CNARecipeTypes(ProcessingRecipe.Factory<EnergisingRecipeParams, ? extends EnergisingRecipe> energisingRecipeFactory) {
        this(() -> new EnergisingRecipe.Serializer<>(energisingRecipeFactory));
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends RecipeSerializer<?>> T getSerializer() {
        return (T) serializerObject;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <I extends RecipeInput, R extends Recipe<I>> RecipeType<R> getType() {
        return (RecipeType<R>) type;
    }

    @Override
    public String getSerializedName() {
        return id.toString();
    }

    public static void load() {
        // Make sure the recipe types load
    }

}