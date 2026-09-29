package org.antarcticgardens.cna.platform;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface DataProviderAdder {
    void addProvider(Function<PackOutput, DataProvider> factory);
    void addProvider(BiFunction<PackOutput, CompletableFuture<HolderLookup.Provider>, DataProvider> factory);
}
