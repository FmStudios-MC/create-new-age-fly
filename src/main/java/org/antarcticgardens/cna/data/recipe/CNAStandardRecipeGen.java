package org.antarcticgardens.cna.data.recipe;

import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.AllItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
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
public class CNAStandardRecipeGen extends CNARecipeProvider {

    // ========================================================================================================== Shaped

    GeneratedRecipe ADVANCED_ENERGISER = builder(CNABlocks.ADVANCED_ENERGISER)
            .unlockedBy(CNAItems.OVERCHARGED_GOLD)
            .shaped(b -> b
                    .define('G', CNAItems.OVERCHARGED_GOLD)
                    .define('E', CNABlocks.BASIC_ENERGISER)
                    .define('R', Blocks.LIGHTNING_ROD)
                    .pattern("GEG")
                    .pattern(" R ")
                    .pattern(" R "));
    
    GeneratedRecipe ADVANCED_MOTOR = builder(CNABlocks.ADVANCED_MOTOR)
            .unlockedBy(CNAItems.OVERCHARGED_IRON)
            .shaped(b -> b
                    .define('N', Tags.Items.NUGGETS_GOLD)
                    .define('S', AllBlocks.SHAFT)
                    .define('C', AllBlocks.BRASS_CASING)
                    .define('I', CNAItems.OVERCHARGED_IRON)
                    .pattern("NNN")
                    .pattern("ICS")
                    .pattern("NNN"));
    
    GeneratedRecipe ADVANCED_SOLAR_HEATING_PLATE = builder(CNABlocks.ADVANCED_SOLAR_HEATING_PLATE)
            .unlockedBy(CNABlocks.HEAT_PIPE)
            .shaped(b -> b
                    .define('P', CNABlocks.HEAT_PIPE)
                    .define('I', CNAItems.OVERCHARGED_IRON)
                    .define('G', Tags.Items.GLASS_BLOCKS_COLORLESS)
                    .pattern("GGG")
                    .pattern("IPI")
                    .pattern("IPI"));
    
    GeneratedRecipe BASIC_MOTOR = builder(CNABlocks.BASIC_MOTOR)
            .unlockedBy(CNABlocks.MAGNETITE_BLOCK)
            .shaped(b -> b
                    .define('N', Tags.Items.NUGGETS_IRON)
                    .define('S', AllBlocks.SHAFT)
                    .define('C', AllBlocks.ANDESITE_CASING)
                    .define('M', CNABlocks.MAGNETITE_BLOCK)
                    .pattern("NNN")
                    .pattern("MCS")
                    .pattern("NNN"));
    
    GeneratedRecipe BASIC_MOTOR_EXTENSION = builder(CNABlocks.BASIC_MOTOR_EXTENSION)
            .unlockedBy(CNABlocks.BASIC_MOTOR)
            .shaped(b -> b
                    .define('C', CNAItems.COPPER_CIRCUIT)
                    .define('M', CNABlocks.BASIC_MOTOR)
                    .define('I', CNAItems.OVERCHARGED_IRON)
                    .pattern("III")
                    .pattern("MCC")
                    .pattern("III"));
    
    GeneratedRecipe BASIC_SOLAR_HEATING_PLATE = builder(CNABlocks.BASIC_SOLAR_HEATING_PLATE)
            .unlockedBy(CNABlocks.HEAT_PIPE)
            .shaped(b -> b
                    .define('P', CNABlocks.HEAT_PIPE)
                    .define('I', Tags.Items.INGOTS_IRON)
                    .define('G', Tags.Items.GLASS_BLOCKS_COLORLESS)
                    .pattern("GGG")
                    .pattern("IPI")
                    .pattern("IPI"));
    
    GeneratedRecipe HEATER = builder(CNABlocks.HEATER)
            .unlockedBy(CNABlocks.HEAT_PIPE)
            .shaped(b -> b
                    .define('P', CNABlocks.HEAT_PIPE)
                    .define('I', CNAItems.OVERCHARGED_IRON)
                    .define('N', Tags.Items.NUGGETS_IRON)
                    .define('B', AllItems.EMPTY_BLAZE_BURNER)
                    .pattern("N N")
                    .pattern("NBN")
                    .pattern("PIP"));
    
    GeneratedRecipe CARBON_BRUSHES = builder(CNABlocks.CARBON_BRUSHES)
            .unlockedBy(CNABlocks.GENERATOR_COIL)
            .shaped(b -> b
                    .define('A', AllItems.ANDESITE_ALLOY)
                    .define('C', Items.COAL)
                    .define('S', AllBlocks.SHAFT)
                    .pattern("AAA")
                    .pattern("CSC")
                    .pattern("AAA"));
    
    GeneratedRecipe ELECTRICAL_CONNECTOR = builder(CNABlocks.ELECTRICAL_CONNECTOR)
            .unlockedBy(CNATags.Common.NUGGETS_COPPER)
            .amount(2)
            .shaped(b -> b
                    .define('A', AllItems.ANDESITE_ALLOY)
                    .define('C', CNATags.Common.NUGGETS_COPPER)
                    .pattern("AC")
                    .pattern("CA")
                    .pattern("AC"));
    
    GeneratedRecipe FLUXUATED_MAGNETITE = builder(CNABlocks.FLUXUATED_MAGNETITE)
            .unlockedBy(CNAItems.OVERCHARGED_DIAMOND)
            .amount(2)
            .shaped(b -> b
                    .define('G', CNAItems.OVERCHARGED_GOLD)
                    .define('D', CNAItems.OVERCHARGED_DIAMOND)
                    .define('M', CNABlocks.MAGNETITE_BLOCK)
                    .pattern("GMG")
                    .pattern("MDM")
                    .pattern("GMG"));
    
    GeneratedRecipe GENERATOR_COIL = builder(CNABlocks.GENERATOR_COIL)
            .unlockedBy(Tags.Items.INGOTS_COPPER)
            .shaped(b -> b
                    .define('C', Tags.Items.INGOTS_COPPER) // TODO: Maybe we should use copper wires instead?
                    .define('A', AllBlocks.ANDESITE_ALLOY_BLOCK)
                    .pattern("CCC")
                    .pattern("CAC")
                    .pattern("CCC"));
    
    GeneratedRecipe HEAT_PIPE = builder(CNABlocks.HEAT_PIPE)
            .unlockedBy(CNATags.Common.PLATES_COPPER)
            .amount(4)
            .shaped(b -> b
                    .define('C', CNATags.Common.PLATES_COPPER) 
                    .define('N', CNATags.Common.NUGGETS_ZINC)
                    .define('T', Blocks.TERRACOTTA)
                    .pattern("NTN")
                    .pattern("TCT"));

    GeneratedRecipe HEAT_PIPE_MIRRORED = builder(CNABlocks.HEAT_PIPE)
            .unlockedBy(CNATags.Common.PLATES_COPPER)
            .suffix("_mirrored")
            .amount(4)
            .shaped(b -> b
                    .define('C', CNATags.Common.PLATES_COPPER)
                    .define('N', CNATags.Common.NUGGETS_ZINC)
                    .define('T', Blocks.TERRACOTTA)
                    .pattern("TCT")
                    .pattern("NTN"));

    GeneratedRecipe HEAT_PUMP = builder(CNABlocks.HEAT_PUMP)
            .unlockedBy(CNAItems.THORIUM, CNABlocks.HEAT_PIPE)
            .amount(4)
            .shaped(b -> b
                    .define('P', CNABlocks.HEAT_PIPE)
                    .define('T', CNAItems.THORIUM) // TODO tag?
                    .pattern("PTP"));

    GeneratedRecipe LAYERED_MAGNET = builder(CNABlocks.LAYERED_MAGNET)
            .unlockedBy(CNAItems.OVERCHARGED_GOLD)
            .amount(4)
            .shaped(b -> b
                    .define('I', CNAItems.OVERCHARGED_IRON)
                    .define('G', CNAItems.OVERCHARGED_GOLD)
                    .pattern("III")
                    .pattern("GGG")
                    .pattern("III"));

    GeneratedRecipe NETHERITE_MAGNET = builder(CNABlocks.NETHERITE_MAGNET)
            .unlockedBy(Items.NETHERITE_SCRAP)
            .amount(2)
            .shaped(b -> b
                    .define('D', CNAItems.OVERCHARGED_DIAMOND)
                    .define('S', Items.NETHERITE_SCRAP)
                    .pattern("SDS")
                    .pattern("DDD")
                    .pattern("SDS"));

    GeneratedRecipe REDSTONE_MAGNET = builder(CNABlocks.REDSTONE_MAGNET)
            .unlockedBy(Tags.Items.DUSTS_REDSTONE)
            .amount(2)
            .shaped(b -> b
                    .define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                    .define('N', Tags.Items.NUGGETS_IRON)
                    .pattern("NNN")
                    .pattern("NRN")
                    .pattern("NNN"));
    
    GeneratedRecipe REACTOR_FUEL_ACCEPTOR = builder(CNABlocks.REACTOR_FUEL_ACCEPTOR)
            .unlockedBy(CNABlocks.REACTOR_CASING)
            .shaped(b -> b
                    .define('C', CNABlocks.REACTOR_CASING)
                    .define('A', AllBlocks.ANDESITE_FUNNEL)
                    .define('B', AllBlocks.BRASS_FUNNEL)
                    .pattern("CAC")
                    .pattern("CBC"));

    GeneratedRecipe REACTOR_GLASS = builder(CNABlocks.REACTOR_GLASS)
            .unlockedBy(CNABlocks.REACTOR_CASING)
            .shaped(b -> b
                    .define('C', CNABlocks.REACTOR_CASING)
                    .define('G', Tags.Items.GLASS_BLOCKS_COLORLESS)
                    .pattern("CGC")
                    .pattern("GGG")
                    .pattern("CGC"));

    GeneratedRecipe REACTOR_HEAT_VENT = builder(CNABlocks.REACTOR_HEAT_VENT)
            .unlockedBy(CNABlocks.REACTOR_CASING)
            .shapeless(b -> b
                    .requires(CNABlocks.REACTOR_CASING, 2)
                    .requires(CNABlocks.HEAT_PIPE, 2));

    GeneratedRecipe REINFORCED_ENERGISER = builder(CNABlocks.REINFORCED_ENERGISER)
            .unlockedBy(CNAItems.OVERCHARGED_DIAMOND)
            .shaped(b -> b
                    .define('D', CNAItems.OVERCHARGED_DIAMOND)
                    .define('E', CNABlocks.ADVANCED_ENERGISER)
                    .define('d', Tags.Items.GEMS_DIAMOND)
                    .define('C', Tags.Items.STORAGE_BLOCKS_COPPER)
                    .define('c', Tags.Items.INGOTS_COPPER)
                    .pattern("DED")
                    .pattern("dcd")
                    .pattern(" C "));

    GeneratedRecipe STIRLING_ENGINE = builder(CNABlocks.STIRLING_ENGINE)
            .unlockedBy(CNABlocks.HEAT_PIPE)
            .shaped(b -> b
                    .define('S', AllBlocks.SHAFT)
                    .define('P', CNABlocks.HEAT_PIPE)
                    .define('C', Tags.Items.STORAGE_BLOCKS_COPPER)
                    .define('N', Tags.Items.NUGGETS_IRON)
                    .pattern("NSN")
                    .pattern("PCP"));

    GeneratedRecipe COPPER_WIRE_BLOCK = builder(CNABlocks.COPPER_WIRE_BLOCK)
            .unlockedBy(CNAItems.COPPER_WIRE)
            .shaped(b -> b
                    .define('W', CNAItems.COPPER_WIRE)
                    .pattern("WW")
                    .pattern("WW"));

    GeneratedRecipe OVERCHARGED_IRON_WIRE_BLOCK = builder(CNABlocks.OVERCHARGED_IRON_WIRE_BLOCK)
            .unlockedBy(CNAItems.OVERCHARGED_IRON_WIRE)
            .shaped(b -> b
                    .define('W', CNAItems.OVERCHARGED_IRON_WIRE)
                    .pattern("WW")
                    .pattern("WW"));

    GeneratedRecipe OVERCHARGED_GOLDEN_WIRE_BLOCK = builder(CNABlocks.OVERCHARGED_GOLDEN_WIRE_BLOCK)
            .unlockedBy(CNAItems.OVERCHARGED_GOLDEN_WIRE)
            .shaped(b -> b
                    .define('W', CNAItems.OVERCHARGED_GOLDEN_WIRE)
                    .pattern("WW")
                    .pattern("WW"));

    GeneratedRecipe OVERCHARGED_DIAMOND_WIRE_BLOCK = builder(CNABlocks.OVERCHARGED_DIAMOND_WIRE_BLOCK)
            .unlockedBy(CNAItems.OVERCHARGED_DIAMOND_WIRE)
            .shaped(b -> b
                    .define('W', CNAItems.OVERCHARGED_DIAMOND_WIRE)
                    .pattern("WW")
                    .pattern("WW"));

    GeneratedRecipe STREET_LIGHT = builder(CNABlocks.STREET_LIGHT)
            .unlockedBy(CNABlocks.ELECTRICAL_CONNECTOR)
            .shaped(b -> b
                    .define('S', CNATags.Common.PLATES_IRON)
                    .define('G', Tags.Items.GLASS_BLOCKS_COLORLESS)
                    .define('C', CNABlocks.ELECTRICAL_CONNECTOR)
                    .pattern("S")
                    .pattern("G")
                    .pattern("C"));

    GeneratedRecipe LAMP_POST = builder(CNABlocks.LAMP_POST)
            .amount(8)
            .unlockedBy(AllItems.ANDESITE_ALLOY)
            .shaped(b -> b
                    .define('S', CNATags.Common.PLATES_IRON)
                    .define('A', AllItems.ANDESITE_ALLOY)
                    .pattern("S")
                    .pattern("A")
                    .pattern("A"));

    // ======================================================================================================= Shapeless
    
    GeneratedRecipe BASIC_ENERGISER = builder(CNABlocks.BASIC_ENERGISER)
            .unlockedBy(AllBlocks.ANDESITE_CASING)
            .shapeless(b -> b.requires(AllBlocks.ANDESITE_CASING)
                    .requires(Blocks.LIGHTNING_ROD));

    GeneratedRecipe COPPER_WIRE_FROM_BLOCK = builder(CNAItems.COPPER_WIRE)
            .unlockedBy(CNAItems.COPPER_WIRE)
            .amount(4)
            .shapeless(b -> b.requires(CNABlocks.COPPER_WIRE_BLOCK));

    GeneratedRecipe OVERCHARGED_IRON_WIRE_FROM_BLOCK = builder(CNAItems.OVERCHARGED_IRON_WIRE)
            .unlockedBy(CNAItems.OVERCHARGED_IRON_WIRE)
            .amount(4)
            .shapeless(b -> b.requires(CNABlocks.OVERCHARGED_IRON_WIRE_BLOCK));

    GeneratedRecipe OVERCHARGED_GOLDEN_WIRE_FROM_BLOCK = builder(CNAItems.OVERCHARGED_GOLDEN_WIRE)
            .unlockedBy(CNAItems.OVERCHARGED_GOLDEN_WIRE)
            .amount(4)
            .shapeless(b -> b.requires(CNABlocks.OVERCHARGED_GOLDEN_WIRE_BLOCK));

    GeneratedRecipe OVERCHARGED_DIAMOND_WIRE_FROM_BLOCK = builder(CNAItems.OVERCHARGED_DIAMOND_WIRE)
            .unlockedBy(CNAItems.OVERCHARGED_DIAMOND_WIRE)
            .amount(4)
            .shapeless(b -> b.requires(CNABlocks.OVERCHARGED_DIAMOND_WIRE_BLOCK));

    // =================================================================================================================
    
    protected GeneratedRecipeBuilder builder(ItemLike result) {
        return new GeneratedRecipeBuilder(result);
    }
    
    protected class GeneratedRecipeBuilder extends GeneratedRecipeBuilderBase<GeneratedRecipeBuilder> {
        public GeneratedRecipeBuilder(ItemLike result) {
            super(result);
        }

        protected GeneratedRecipe shaped(UnaryOperator<ShapedRecipeBuilder> operator) {
            return register(consumer -> {
                ShapedRecipeBuilder builder = operator.apply(ShapedRecipeBuilder.shaped(category, result, amount));
                
                if (trigger != null) {
                    builder.unlockedBy("criterion", trigger);
                }
                
                builder.save(consumer, createLocation("shaped"));
            });
        }

        protected GeneratedRecipe shapeless(UnaryOperator<ShapelessRecipeBuilder> operator) {
            return register(consumer -> {
                ShapelessRecipeBuilder builder = operator.apply(ShapelessRecipeBuilder.shapeless(category, result, amount));

                if (trigger != null) {
                    builder.unlockedBy("criterion", trigger);
                }

                builder.save(consumer, createLocation("shapeless"));
            });
        }
    }

    @Override
    public String getName() {
        return "Create New Age Standard Recipes";
    }

    public CNAStandardRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }
}
