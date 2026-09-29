package org.antarcticgardens.cna.data.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import org.antarcticgardens.cna.CNABlocks;
import org.antarcticgardens.cna.CreateNewAge;

public class CNAConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> THORIUM_ORE = key("thorium_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MAGNETITE_BLOCK = key("magnetite_block");
    
    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> ctx) {
        RuleTest stoneOreReplaceables = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        
        FeatureUtils.register(ctx, THORIUM_ORE, Feature.ORE, 
                new OreConfiguration(stoneOreReplaceables, CNABlocks.THORIUM_ORE.defaultBlockState(), 16, 0.4f));
        FeatureUtils.register(ctx, MAGNETITE_BLOCK, Feature.ORE,
                new OreConfiguration(stoneOreReplaceables, CNABlocks.MAGNETITE_BLOCK.defaultBlockState(), 4, 0.4f));
    }
    
    private static ResourceKey<ConfiguredFeature<?, ?>> key(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath(CreateNewAge.MOD_ID, name));
    }
}
