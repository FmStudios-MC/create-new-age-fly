package org.antarcticgardens.cna;

import com.google.gson.JsonObject;
import com.zurrtum.create.AllSoundEvents;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CNASounds {
    public static final Map<Identifier, AllSoundEvents.SoundEntry> CNASounds = new HashMap<>();

    public static final AllSoundEvents.SoundEntry GEIGER_COUNTER =
            new CNASoundEntryBuilder(Identifier.fromNamespaceAndPath(CreateNewAge.MOD_ID, "geiger_counter"))
                    .subtitle("Geiger Counter Clicking")
                    .category(SoundSource.PLAYERS)
                    .build();

    public static void load() {
        for (AllSoundEvents.SoundEntry entry : CNASounds.values())
            entry.prepare();
    }

    public static class CNASoundEntryBuilder extends AllSoundEvents.SoundEntryBuilder {

        public CNASoundEntryBuilder(Identifier id) {
            super(id);
        }

        @Override
        public AllSoundEvents.SoundEntry build() {
            AllSoundEvents.SoundEntry entry = super.build();
            CNASounds.put(entry.getId(), entry);
            return entry;
        }
    }

    public static class SoundEntryProvider implements DataProvider {

        private PackOutput output;

        public SoundEntryProvider(PackOutput packOutput) {
            output = packOutput;
        }

        @Override
        public CompletableFuture<?> run(CachedOutput cache) {
            return generate(output.getOutputFolder(), cache);
        }

        @Override
        public String getName() {
            return "CNA Custom Sounds";
        }

        public CompletableFuture<?> generate(Path path, CachedOutput cache) {
            path = path.resolve("assets/create_new_age");
            JsonObject json = new JsonObject();
            CNASounds.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        entry.getValue()
                                .write(json);
                    });
            return DataProvider.saveStable(cache, json, path.resolve("sounds.json"));
        }

    }
}

