package org.antarcticgardens.cna.content.energising.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.zurrtum.create.content.processing.recipe.ProcessingOutput;
import com.zurrtum.create.foundation.recipe.CreateSingleStackRollableRecipe;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.antarcticgardens.cna.CNARecipeTypes;

import java.util.List;

/**
 * Create Fly turned processing recipes into records with one ingredient and a result list, and a
 * sequenced assembly step is now any recipe. The JSON shape follows Create Fly's own processing
 * recipes: {@code ingredient} (singular) and {@code results}, plus {@code energy_needed}.
 */
public record EnergisingRecipe(List<ProcessingOutput> results, Ingredient ingredient,
                               int energyNeeded) implements CreateSingleStackRollableRecipe {
    public static final MapCodec<EnergisingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ProcessingOutput.CODEC.listOf(1, 4).fieldOf("results").forGetter(EnergisingRecipe::results),
            Ingredient.CODEC.fieldOf("ingredient").forGetter(EnergisingRecipe::ingredient),
            Codec.INT.optionalFieldOf("energy_needed", 0).forGetter(EnergisingRecipe::energyNeeded)
    ).apply(instance, EnergisingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EnergisingRecipe> STREAM_CODEC = StreamCodec.composite(
            ProcessingOutput.STREAM_CODEC.apply(ByteBufCodecs.list()),
            EnergisingRecipe::results,
            Ingredient.CONTENTS_STREAM_CODEC,
            EnergisingRecipe::ingredient,
            ByteBufCodecs.VAR_INT,
            EnergisingRecipe::energyNeeded,
            EnergisingRecipe::new
    );

    public static final RecipeSerializer<EnergisingRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    public int getEnergyNeeded() {
        return energyNeeded;
    }

    public boolean test(ItemStack stack) {
        return ingredient.test(stack);
    }

    @Override
    public RecipeSerializer<EnergisingRecipe> getSerializer() {
        return CNARecipeTypes.ENERGISING_SERIALIZER;
    }

    @Override
    public RecipeType<EnergisingRecipe> getType() {
        return CNARecipeTypes.ENERGISING;
    }
}
