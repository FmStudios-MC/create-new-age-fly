package org.antarcticgardens.cna.data;

import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import org.antarcticgardens.cna.CreateNewAge;
import org.antarcticgardens.cna.data.worldgen.CNAConfiguredFeatures;
import org.antarcticgardens.cna.data.worldgen.CNAPlacedFeatures;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

public class CNAGeneratedEntriesProvider extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = Util.make(new RegistrySetBuilder(), CNAGeneratedEntriesProvider::addBootstraps);
    
    public CNAGeneratedEntriesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(CreateNewAge.MOD_ID));
    }
    
    public static void addBootstraps(RegistrySetBuilder builder) {
        builder.add(Registries.CONFIGURED_FEATURE, CNAConfiguredFeatures::bootstrap)
                .add(Registries.PLACED_FEATURE, CNAPlacedFeatures::bootstrap);
    }

    @Override
    public String getName() {
        return "Create New Age Generated Entries Provider";
    }
}
