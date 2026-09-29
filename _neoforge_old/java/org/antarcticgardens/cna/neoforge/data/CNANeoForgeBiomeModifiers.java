package org.antarcticgardens.cna.neoforge.data;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.antarcticgardens.cna.CreateNewAge;
import org.antarcticgardens.cna.data.worldgen.CNAPlacedFeatures;

public class CNANeoForgeBiomeModifiers {
    public static ResourceKey<BiomeModifier> THORIUM_ORE = key("thorium_ore");
    public static ResourceKey<BiomeModifier> MAGNETITE_BLOCK = key("magnetite_block");
    
    public static void bootstrap(BootstrapContext<BiomeModifier> ctx) {
        HolderGetter<Biome> biomeLookup = ctx.lookup(Registries.BIOME);
        HolderSet<Biome> overworld = biomeLookup.getOrThrow(BiomeTags.IS_OVERWORLD);
        
        HolderGetter<PlacedFeature> featureLookup = ctx.lookup(Registries.PLACED_FEATURE);
        Holder<PlacedFeature> thoriumOre = featureLookup.getOrThrow(CNAPlacedFeatures.THORIUM_ORE);
        Holder<PlacedFeature> magnetiteBlock = featureLookup.getOrThrow(CNAPlacedFeatures.MAGNETITE_BLOCK);
        
        ctx.register(THORIUM_ORE, addOre(overworld, thoriumOre));
        ctx.register(MAGNETITE_BLOCK, addOre(overworld, magnetiteBlock));
    }

    private static ResourceKey<BiomeModifier> key(String name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath(CreateNewAge.MOD_ID, name));
    }

    private static BiomeModifier addOre(HolderSet<Biome> biomes, Holder<PlacedFeature> feature) {
        return new BiomeModifiers.AddFeaturesBiomeModifier(biomes, HolderSet.direct(feature), GenerationStep.Decoration.UNDERGROUND_ORES);
    }
}
