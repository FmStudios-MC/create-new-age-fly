package org.antarcticgardens.cna.neoforge.data;

import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.antarcticgardens.cna.CreateNewAge;
import org.antarcticgardens.cna.data.CNAGeneratedEntriesProvider;
import org.antarcticgardens.cna.data.CreateNewAgeDatagen;
import org.antarcticgardens.cna.neoforge.NeoForgeDataProviderAdder;

public class CreateNewAgeDatagenNeoForge extends CreateNewAgeDatagen {
    public static void gatherData(GatherDataEvent event) {
        if (!event.getMods().contains(CreateNewAge.MOD_ID))
            return;

        setupDatagen(new NeoForgeDataProviderAdder(event));
        CNAGeneratedEntriesProvider.BUILDER.add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, CNANeoForgeBiomeModifiers::bootstrap);
    }
}
