package org.antarcticgardens.cna.content.energising.recipe;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.sequenced.IAssemblyRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import org.antarcticgardens.cna.CNABlocks;
import org.antarcticgardens.cna.CNARecipeTypes;
import org.antarcticgardens.cna.CreateNewAge;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;

public class EnergisingRecipe extends ProcessingRecipe<RecipeWrapper, EnergisingRecipeParams> implements IAssemblyRecipe {
    private int energyNeeded;

    public EnergisingRecipe(EnergisingRecipeParams params) {
        super(CNARecipeTypes.ENERGISING, params);
        energyNeeded = params.energyNeeded;
    }

    public EnergisingRecipe(IRecipeTypeInfo typeInfo, EnergisingRecipeParams params) {
        super(typeInfo, params);
        energyNeeded = params.energyNeeded;
    }

    @Override
    public boolean matches(RecipeWrapper input, Level level) {
        if (input.isEmpty())
            return false;
        return ingredients.get(0)
                .test(input.getItem(0));
    }

    public static class Builder<R extends EnergisingRecipe> extends ProcessingRecipeBuilder<EnergisingRecipeParams, R, EnergisingRecipe.Builder<R>> {
        public Builder(EnergisingRecipe.Factory<EnergisingRecipeParams, R> factory, Identifier recipeId) {
            super(factory, recipeId);
        }

        @Override
        protected EnergisingRecipeParams createParams() {
            return new EnergisingRecipeParams();
        }

        @Override
        public EnergisingRecipe.Builder<R> self() {
            return this;
        }

        public Builder<R> energyNeeded(int energyNeeded) {
            params.energyNeeded = energyNeeded;
            return this;
        }
    }

    public static class Serializer<R extends EnergisingRecipe> implements RecipeSerializer<R> {
        private final MapCodec<R> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, R> streamCodec;
        private final ProcessingRecipe.Factory<EnergisingRecipeParams, R> factory;

        public Serializer(ProcessingRecipe.Factory<EnergisingRecipeParams, R> factory) {
            this.codec = ProcessingRecipe.codec(factory, EnergisingRecipeParams.CODEC);
            this.streamCodec = ProcessingRecipe.streamCodec(factory, EnergisingRecipeParams.STREAM_CODEC);
            this.factory = factory;
        }

        @Override
        public MapCodec<R> codec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, R> streamCodec() {
            return streamCodec;
        }

        public ProcessingRecipe.Factory<EnergisingRecipeParams, R> factory() {
            return factory;
        }

    }
    
    public int getEnergyNeeded() {
        return energyNeeded;
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 4;
    }

    @Override
    public Component getDescriptionForAssembly() {
        return Component.translatable("recipe.assembly.energising");
    }

    @Override
    public void addRequiredMachines(Set<ItemLike> list) {
        list.add(CNABlocks.BASIC_ENERGISER.get());
    }

    @Override
    public void addAssemblyIngredients(List<Ingredient> list) {}
    
    @SuppressWarnings("unchecked")
    @Override
    public Supplier<Supplier<SequencedAssemblySubCategory>> getJEISubCategory() {
        return (Supplier<Supplier<SequencedAssemblySubCategory>>) CreateNewAge.getInstance().getPlatform().getEnergisingRecipeSubCategory();
    }

    public boolean test(ItemStack stack) {
        return ingredients.get(0)
                .test(stack);
    }
    
    public static void load() {  }
}
