package org.antarcticgardens.cna.neoforge;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.antarcticgardens.cna.platform.DataProviderAdder;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

public class NeoForgeDataProviderAdder implements DataProviderAdder {
    private final GatherDataEvent event;
    
    public NeoForgeDataProviderAdder(GatherDataEvent event) {
        this.event = event;
    }
    
    @Override
    public void addProvider(Function<PackOutput, DataProvider> factory) {
        event.getGenerator().addProvider(true, factory.apply(event.getGenerator().getPackOutput()));
    }

    @Override
    public void addProvider(BiFunction<PackOutput, CompletableFuture<HolderLookup.Provider>, DataProvider> factory) {
        event.getGenerator().addProvider(true, factory.apply(event.getGenerator().getPackOutput(), event.getLookupProvider()));
    }
}
