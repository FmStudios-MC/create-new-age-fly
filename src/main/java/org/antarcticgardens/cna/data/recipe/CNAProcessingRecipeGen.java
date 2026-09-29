package org.antarcticgardens.cna.data.recipe;

import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.AllItems;
import com.zurrtum.create.AllRecipeTypes;
import com.zurrtum.create.content.kinetics.crusher.CrushingRecipe;
import com.zurrtum.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.zurrtum.create.content.kinetics.deployer.ItemApplicationRecipe;
import com.zurrtum.create.content.kinetics.deployer.ManualApplicationRecipe;
import com.zurrtum.create.content.kinetics.mixer.CompactingRecipe;
import com.zurrtum.create.content.kinetics.mixer.MixingRecipe;
import com.zurrtum.create.content.kinetics.press.PressingRecipe;
import com.zurrtum.create.content.kinetics.saw.CuttingRecipe;
import com.simibubi.create.content.processing.recipe.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.antarcticgardens.cna.CNABlocks;
import org.antarcticgardens.cna.CNAItems;
import org.antarcticgardens.cna.CNARecipeTypes;
import org.antarcticgardens.cna.CNATags;
import org.antarcticgardens.cna.content.energising.recipe.EnergisingRecipe;
import org.antarcticgardens.cna.data.FluidConstants;

import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;

import net.neoforged.neoforge.common.Tags;

@SuppressWarnings("unused")
public class CNAProcessingRecipeGen extends CNARecipeProvider {
    // ========================================================================================================= Cutting
    
    GeneratedRecipe COPPER_WIRE = builder(CNAItems.COPPER_WIRE)
            .amount(4)
            .cutting(b -> b
                    .withItemIngredients(Ingredient.of(CNATags.Common.PLATES_COPPER))
                    .duration(100));
    
    GeneratedRecipe OVERCHARGED_GOLDEN_WIRE = builder(CNAItems.OVERCHARGED_GOLDEN_WIRE)
            .amount(4)
            .cutting(b -> b
                    .withItemIngredients(Ingredient.of(CNAItems.OVERCHARGED_GOLDEN_SHEET))
                    .duration(100));

    GeneratedRecipe OVERCHARGED_IRON_WIRE = builder(CNAItems.OVERCHARGED_IRON_WIRE)
            .amount(4)
            .cutting(b -> b
                    .withItemIngredients(Ingredient.of(CNAItems.OVERCHARGED_IRON_SHEET))
                    .duration(100));

    // ======================================================================================================= Manual Application

    GeneratedRecipe HEAT_CASING = builder(CNABlocks.HEAT_CASING)
            .manualApplication(b -> b
                    .withItemIngredients(Ingredient.of(AllBlocks.INDUSTRIAL_IRON_BLOCK), Ingredient.of(AllItems.ZINC_INGOT)));

    // ======================================================================================================= Deploying
    
    GeneratedRecipe COPPER_CIRCUIT = builder(CNAItems.COPPER_CIRCUIT)
            .deploying(b -> b
                    .withItemIngredients(Ingredient.of(CNAItems.BLANK_CIRCUIT), Ingredient.of(CNAItems.COPPER_WIRE)));

    // ====================================================================================================== Energising
    
    GeneratedRecipe OVERCHARGED_DIAMOND = builder(CNAItems.OVERCHARGED_DIAMOND)
            .energising(b -> b
                    .energyNeeded(10000)
                    .withItemIngredients(Ingredient.of(Tags.Items.GEMS_DIAMOND)));

    GeneratedRecipe OVERCHARGED_GOLD = builder(CNAItems.OVERCHARGED_GOLD)
            .energising(b -> b
                    .energyNeeded(2000)
                    .withItemIngredients(Ingredient.of(Tags.Items.INGOTS_GOLD)));

    GeneratedRecipe OVERCHARGED_GOLDEN_SHEET_ENERGISING = builder(CNAItems.OVERCHARGED_GOLDEN_SHEET)
            .energising(b -> b
                    .energyNeeded(2000)
                    .withItemIngredients(Ingredient.of(CNATags.Common.PLATES_GOLD)));

    GeneratedRecipe OVERCHARGED_IRON = builder(CNAItems.OVERCHARGED_IRON)
            .energising(b -> b
                    .energyNeeded(1000)
                    .withItemIngredients(Ingredient.of(Tags.Items.INGOTS_IRON)));

    GeneratedRecipe OVERCHARGED_IRON_SHEET_ENERGISING = builder(CNAItems.OVERCHARGED_IRON_SHEET)
            .energising(b -> b
                    .energyNeeded(1000)
                    .withItemIngredients(Ingredient.of(CNATags.Common.PLATES_IRON)));
    
    GeneratedRecipe EXPERIENCE_BOTTLE = builder(Items.EXPERIENCE_BOTTLE)
            .energising(b -> b
                    .energyNeeded(50000)
                    .withItemIngredients(Ingredient.of(Items.GLASS_BOTTLE)));

    // ====================================================================================================== Compacting
    
    GeneratedRecipe BLANK_CIRCUIT = builder(CNAItems.BLANK_CIRCUIT)
            .amount(4)
            .compacting(b -> b
                    .withItemIngredients(
                            Ingredient.of(Tags.Items.STONES),
                            Ingredient.of(Tags.Items.DUSTS_REDSTONE), 
                            Ingredient.of(Tags.Items.INGOTS_IRON), 
                            Ingredient.of(Items.QUARTZ))
                    .requiresHeat(HeatCondition.SUPERHEATED));

    // ======================================================================================================== Pressing
    
    GeneratedRecipe OVERCHARGED_GOLDEN_SHEET_PRESSING = builder(CNAItems.OVERCHARGED_GOLDEN_SHEET)
            .pressing(b -> b.withItemIngredients(Ingredient.of(CNAItems.OVERCHARGED_GOLD)));

    GeneratedRecipe OVERCHARGED_IRON_SHEET_PRESSING = builder(CNAItems.OVERCHARGED_IRON_SHEET)
            .pressing(b -> b.withItemIngredients(Ingredient.of(CNAItems.OVERCHARGED_IRON)));

    // ======================================================================================================== Crushing
    
    GeneratedRecipe RADIOACTIVE_THORIUM = builder(null)
            .name("radioactive_thorium")
            .crushing(b -> b
                    .withItemIngredients(Ingredient.of(CNAItems.THORIUM))
                    .output(0.1f, CNAItems.RADIOACTIVE_THORIUM)
                    .output(0.7f, Items.IRON_NUGGET)
                    .output(0.75f, AllItems.EXP_NUGGET)
                    .duration(400));

    // ========================================================================================================== Mixing
    
    GeneratedRecipe THORIUM = builder(CNAItems.THORIUM)
            .amount(2)
            .mixing(b -> b
                    .withItemIngredients(
                            Ingredient.of(CNAItems.THORIUM),
                            Ingredient.of(Tags.Items.STONES),
                            Ingredient.of(Items.CLAY))
                    .withFluidIngredients(SizedFluidIngredient.of(Fluids.WATER.getSource(), FluidConstants.BUCKET)));
    
    // =================================================================================================================
    
    protected GeneratedRecipeBuilder builder(ItemLike result) {
        return new GeneratedRecipeBuilder(result);
    }
    
    protected class GeneratedRecipeBuilder extends GeneratedRecipeBuilderBase<GeneratedRecipeBuilder> {
        public GeneratedRecipeBuilder(ItemLike result) {
            super(result);
        }
        
        protected GeneratedRecipe cutting(UnaryOperator<StandardProcessingRecipe.Builder<CuttingRecipe>> operator) {
            StandardProcessingRecipe.Serializer<CuttingRecipe> serializer = AllRecipeTypes.CUTTING.getSerializer();
            return create(operator, serializer.factory());
        }

        protected GeneratedRecipe manualApplication(UnaryOperator<ItemApplicationRecipe.Builder<ManualApplicationRecipe>> operator) {
            return register(consumer -> {
                ItemApplicationRecipe.Builder<ManualApplicationRecipe> builder = new ItemApplicationRecipe.Builder<>(ManualApplicationRecipe::new, createLocation());
                if (result != null) {
                    builder.output(result, amount);
                }
                operator.apply(builder).build(consumer);
            });
        }
        
        protected GeneratedRecipe deploying(UnaryOperator<ItemApplicationRecipe.Builder<DeployerApplicationRecipe>> operator) {
            return register(consumer -> {
                ItemApplicationRecipe.Builder<DeployerApplicationRecipe> builder = new ItemApplicationRecipe.Builder<>(DeployerApplicationRecipe::new, createLocation());
                if (result != null) {
                    builder.output(result, amount);
                }
                operator.apply(builder).build(consumer);
            });
        }
        
        protected GeneratedRecipe compacting(UnaryOperator<StandardProcessingRecipe.Builder<CompactingRecipe>> operator) {
            StandardProcessingRecipe.Serializer<CompactingRecipe> serializer = AllRecipeTypes.COMPACTING.getSerializer();
            return create(operator, serializer.factory());
        }

        protected GeneratedRecipe pressing(UnaryOperator<StandardProcessingRecipe.Builder<PressingRecipe>> operator) {
            StandardProcessingRecipe.Serializer<PressingRecipe> serializer = AllRecipeTypes.PRESSING.getSerializer();
            return create(operator, serializer.factory());
        }

        protected GeneratedRecipe crushing(UnaryOperator<StandardProcessingRecipe.Builder<CrushingRecipe>> operator) {
            StandardProcessingRecipe.Serializer<CrushingRecipe> serializer = AllRecipeTypes.CRUSHING.getSerializer();
            return create(operator, serializer.factory());
        }

        protected GeneratedRecipe mixing(UnaryOperator<StandardProcessingRecipe.Builder<MixingRecipe>> operator) {
            StandardProcessingRecipe.Serializer<MixingRecipe> serializer = AllRecipeTypes.MIXING.getSerializer();
            return create(operator, serializer.factory());
        }

        protected GeneratedRecipe energising(UnaryOperator<EnergisingRecipe.Builder<EnergisingRecipe>> operator) {
            EnergisingRecipe.Serializer<EnergisingRecipe> serializer = CNARecipeTypes.ENERGISING.getSerializer();
            return register(consumer -> {
                EnergisingRecipe.Builder<EnergisingRecipe> builder = new EnergisingRecipe.Builder<EnergisingRecipe>(serializer.factory(), createLocation());
                if (result != null) {
                    builder.output(result, amount);
                }
                operator.apply(builder).build(consumer);
            });
        }

        protected <T extends StandardProcessingRecipe<?>> GeneratedRecipe create(UnaryOperator<StandardProcessingRecipe.Builder<T>> operator,
                                                                         StandardProcessingRecipe.Factory<T> factory) {
            return register(consumer -> {
                StandardProcessingRecipe.Builder<T> builder = new StandardProcessingRecipe.Builder<>(factory, createLocation());
                if (result != null) {
                    builder.output(result, amount);
                }
                operator.apply(builder).build(consumer);
            });
        }
    }

    @Override
    public String getName() {
        return "Create New Age Processing Recipes";
    }

    public CNAProcessingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }
}
