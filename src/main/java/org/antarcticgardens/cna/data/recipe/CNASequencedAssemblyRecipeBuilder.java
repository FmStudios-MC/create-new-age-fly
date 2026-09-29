package org.antarcticgardens.cna.data.recipe;

import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.zurrtum.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeBuilder;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.antarcticgardens.cna.content.energising.recipe.EnergisingRecipe;
import org.antarcticgardens.cna.content.energising.recipe.EnergisingRecipeParams;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public class CNASequencedAssemblyRecipeBuilder extends SequencedAssemblyRecipeBuilder {
    public CNASequencedAssemblyRecipeBuilder(Identifier id) {
        super(id);
    }

    @Override
    public <T extends StandardProcessingRecipe<?>> CNASequencedAssemblyRecipeBuilder addStep(StandardProcessingRecipe.Factory<T> factory,
                                                                                     UnaryOperator<StandardProcessingRecipe.Builder<T>> builder) {
        super.addStep(factory, builder);
        return this;
    }

    public CNASequencedAssemblyRecipeBuilder addEnergisingStep(UnaryOperator<EnergisingRecipe.Builder<EnergisingRecipe>> builder) {
        addStep((EnergisingRecipe.Factory<EnergisingRecipeParams, EnergisingRecipe>) EnergisingRecipe::new,
                builder);
        return this;
    }

    public <R extends EnergisingRecipe> SequencedAssemblyRecipeBuilder addStep(
            EnergisingRecipe.Factory<EnergisingRecipeParams, R> factory,
            UnaryOperator<EnergisingRecipe.Builder<R>> builder) {
        return addStep((Function<Identifier, EnergisingRecipe.Builder<R>>)
                id -> new EnergisingRecipe.Builder<>(factory, id), builder);
    }

    @Override
    public CNASequencedAssemblyRecipeBuilder require(ItemLike ingredient) {
        super.require(ingredient);
        return this;
    }

    @Override
    public CNASequencedAssemblyRecipeBuilder require(TagKey<Item> tag) {
        super.require(tag);
        return this;
    }

    @Override
    public CNASequencedAssemblyRecipeBuilder require(Ingredient ingredient) {
        super.require(ingredient);
        return this;
    }

    @Override
    public CNASequencedAssemblyRecipeBuilder transitionTo(ItemLike item) {
        super.transitionTo(item);
        return this;
    }

    @Override
    public CNASequencedAssemblyRecipeBuilder loops(int loops) {
        super.loops(loops);
        return this;
    }

    @Override
    public CNASequencedAssemblyRecipeBuilder addOutput(ItemLike item, float weight) {
        super.addOutput(item, weight);
        return this;
    }

    @Override
    public CNASequencedAssemblyRecipeBuilder addOutput(ItemStack item, float weight) {
        super.addOutput(item, weight);
        return this;
    }
}
