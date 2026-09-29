package org.antarcticgardens.cna.data.worldgen;

import com.zurrtum.create.infrastructure.worldgen.ConfigPlacementFilter;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;
import org.antarcticgardens.cna.CreateNewAge;

import java.util.List;

public class CNAPlacedFeatures {
    public static final ResourceKey<PlacedFeature> THORIUM_ORE = key("thorium_ore");
    public static final ResourceKey<PlacedFeature> MAGNETITE_BLOCK = key("magnetite_block");
    
    public static void bootstrap(BootstrapContext<PlacedFeature> ctx) {
        HolderGetter<ConfiguredFeature<?, ?>> featureLookup = ctx.lookup(Registries.CONFIGURED_FEATURE);
        Holder<ConfiguredFeature<?, ?>> thoriumOre = featureLookup.getOrThrow(CNAConfiguredFeatures.THORIUM_ORE);
        Holder<ConfiguredFeature<?, ?>> magnetiteBlock = featureLookup.getOrThrow(CNAConfiguredFeatures.MAGNETITE_BLOCK);
        
        PlacementUtils.register(ctx, THORIUM_ORE, thoriumOre, placement(CountPlacement.of(2), 20, 120));
        PlacementUtils.register(ctx, MAGNETITE_BLOCK, magnetiteBlock, placement(CountPlacement.of(15), -20, 60));
    }

    private static ResourceKey<PlacedFeature> key(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(CreateNewAge.MOD_ID, name));
    }

    public static List<PlacementModifier> placement(PlacementModifier frequency, int minHeight, int maxHeight) {
        return List.of(
                frequency,
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(minHeight), VerticalAnchor.absolute(maxHeight)),
                ConfigPlacementFilter.INSTANCE
        );
    }
}
