package org.antarcticgardens.cna.data.recipe;

import com.zurrtum.create.AllBlocks;
import com.simibubi.create.api.data.recipe.MechanicalCraftingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.ItemLike;
import org.antarcticgardens.cna.CNABlocks;
import org.antarcticgardens.cna.CNAItems;
import org.antarcticgardens.cna.CNATags;

import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;

import net.neoforged.neoforge.common.Tags;

@SuppressWarnings("unused")
public class CNAMechanicalCraftingRecipeGen extends CNARecipeProvider {
    GeneratedRecipe ADVANCED_MOTOR_EXTENSION = builder(CNABlocks.ADVANCED_MOTOR_EXTENSION)
            .amount(2)
            .mechanicalCrafting(b -> b
                    .key('D', CNAItems.OVERCHARGED_DIAMOND)
                    .key('M', CNABlocks.REINFORCED_MOTOR)
                    .key('C', CNAItems.COPPER_CIRCUIT)
                    .key('S', CNAItems.OVERCHARGED_IRON_SHEET)
                    .patternLine("SSSSS")
                    .patternLine("DCMCD")
                    .patternLine("SSSSS"));
    
    GeneratedRecipe REINFORCED_MOTOR = builder(CNABlocks.REINFORCED_MOTOR)
            .amount(2)
            .mechanicalCrafting(b -> b
                    .key('D', CNAItems.OVERCHARGED_DIAMOND)
                    .key('d', Tags.Items.GEMS_DIAMOND)
                    .key('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                    .key('S', AllBlocks.SHAFT)
                    .key('C', AllBlocks.BRASS_CASING)
                    .key('P', CNATags.Common.PLATES_IRON)
                    .patternLine("dDPPd")
                    .patternLine("DCRSS")
                    .patternLine("dDPPd"));
    
    GeneratedRecipe REACTOR_ROD = builder(CNABlocks.REACTOR_ROD)
            .amount(2)
            .mechanicalCrafting(b -> b
                    .key('P', CNATags.Common.PLATES_GOLD)
                    .key('F', CNAItems.NUCLEAR_FUEL)
                    .key('C', CNABlocks.REACTOR_CASING)
                    .key('G', CNABlocks.REACTOR_GLASS)
                    .patternLine("CPPPC")
                    .patternLine(" GFG ")
                    .patternLine(" GFG ")
                    .patternLine("CPPPC"));
    
    // =================================================================================================================
    
    protected GeneratedRecipeBuilder builder(ItemLike result) {
        return new GeneratedRecipeBuilder(result);
    }

    protected class GeneratedRecipeBuilder extends GeneratedRecipeBuilderBase<GeneratedRecipeBuilder> {
        public GeneratedRecipeBuilder(ItemLike result) {
            super(result);
        }
        
        protected GeneratedRecipe mechanicalCrafting(UnaryOperator<MechanicalCraftingRecipeBuilder> operator) {
            return register(consumer -> operator.apply(MechanicalCraftingRecipeBuilder.shapedRecipe(result, amount))
                    .build(consumer, createLocation("mechanical_crafting")));
        }
    }
    
    @Override
    public String getName() {
        return "Create New Age Mechanical Crafting Recipes";
    }

    public CNAMechanicalCraftingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }
}
