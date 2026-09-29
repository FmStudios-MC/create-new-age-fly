package org.antarcticgardens.cna.data.recipe;

import com.zurrtum.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.zurrtum.create.content.kinetics.press.PressingRecipe;
import com.zurrtum.create.content.kinetics.saw.CuttingRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import org.antarcticgardens.cna.CNABlocks;
import org.antarcticgardens.cna.CNAItems;
import org.antarcticgardens.cna.CNATags;

import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;

import net.neoforged.neoforge.common.Tags;

@SuppressWarnings("unused")
public class CNASequencedAssemblyRecipeGen extends CNARecipeProvider {
    GeneratedRecipe OVERCHARGED_DIAMOND_WIRE = builder(CNAItems.OVERCHARGED_DIAMOND_WIRE)
            .amount(2)
            .sequencedAssembly(b -> (CNASequencedAssemblyRecipeBuilder) b
                    .transitionTo(CNAItems.INCOMPLETE_WIRE)
                    .require(CNAItems.OVERCHARGED_DIAMOND)
                    .loops(3)
                    .addStep(CuttingRecipe::new, rb -> rb)
                    .addEnergisingStep(rb -> rb.energyNeeded(100)));
    
    GeneratedRecipe ENCHANTED_GOLDEN_APPLE = builder(Items.ENCHANTED_GOLDEN_APPLE)
            .sequencedAssembly(b -> (CNASequencedAssemblyRecipeBuilder) b
                    .transitionTo(CNAItems.INCOMPLETE_ENCHANTED_GOLDEN_APPLE)
                    .require(Items.APPLE)
                    .loops(4)
                    .addEnergisingStep(rb -> rb.energyNeeded(2000000))
                    .addStep(DeployerApplicationRecipe::new, rb -> rb.require(Tags.Items.STORAGE_BLOCKS_GOLD))
                    .addStep(DeployerApplicationRecipe::new, rb -> rb.require(Tags.Items.STORAGE_BLOCKS_GOLD)));

    GeneratedRecipe NUCLEAR_FUEL = builder(CNAItems.NUCLEAR_FUEL)
            .sequencedAssembly(b -> (CNASequencedAssemblyRecipeBuilder) b
                    .transitionTo(CNAItems.INCOMPLETE_FUEL)
                    .require(CNAItems.RADIOACTIVE_THORIUM)
                    .loops(1)
                    .addStep(PressingRecipe::new, rb -> rb)
                    .addStep(DeployerApplicationRecipe::new, rb -> rb.require(CNATags.Common.PLATES_IRON))
                    .addStep(PressingRecipe::new, rb -> rb));

    GeneratedRecipe REACTOR_CASING = builder(CNABlocks.REACTOR_CASING)
            .amount(4)
            .sequencedAssembly(b -> (CNASequencedAssemblyRecipeBuilder) b
                    .transitionTo(CNAItems.INCOMPLETE_REACTOR_CASING)
                    .require(Blocks.BRICKS)
                    .loops(1)
                    .addEnergisingStep(rb -> rb.energyNeeded(500))
                    .addStep(DeployerApplicationRecipe::new, rb -> rb.require(CNATags.Common.PLATES_IRON))
                    .addStep(PressingRecipe::new, rb -> rb));
    
    // =================================================================================================================

    protected GeneratedRecipeBuilder builder(ItemLike result) {
        return new GeneratedRecipeBuilder(result);
    }

    protected class GeneratedRecipeBuilder extends GeneratedRecipeBuilderBase<GeneratedRecipeBuilder> {
        public GeneratedRecipeBuilder(ItemLike result) {
            super(result);
        }

        protected GeneratedRecipe sequencedAssembly(UnaryOperator<CNASequencedAssemblyRecipeBuilder> operator) {
            return register(consumer -> operator.apply(new CNASequencedAssemblyRecipeBuilder(createLocation()))
                    .addOutput(new ItemStack(result, amount), 1).build(consumer));
        }
    }
    
    @Override
    public String getName() {
        return "Create New Age Sequenced Assembly Recipes";
    }
    
    public CNASequencedAssemblyRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
/**/    }
}
