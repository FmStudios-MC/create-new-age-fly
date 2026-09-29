package org.antarcticgardens.cna.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.tterrag.registrate.providers.ProviderType;
import org.antarcticgardens.cna.CNASounds;
import org.antarcticgardens.cna.CreateNewAge;
import org.antarcticgardens.cna.CNATags;
import org.antarcticgardens.cna.data.recipe.CNAMechanicalCraftingRecipeGen;
import org.antarcticgardens.cna.data.recipe.CNAProcessingRecipeGen;
import org.antarcticgardens.cna.data.recipe.CNASequencedAssemblyRecipeGen;
import org.antarcticgardens.cna.data.recipe.CNAStandardRecipeGen;
import org.antarcticgardens.cna.platform.DataProviderAdder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.util.Map;

import static org.antarcticgardens.cna.CreateNewAge.REGISTRATE;

public class CreateNewAgeDatagen {
    protected static void setupDatagen(DataProviderAdder providerConsumer) {
        REGISTRATE.addDataGenerator(ProviderType.LANG, provider -> {
            try {
                File defaultLang = new File(ClassLoader.getSystemResource("assets/create_new_age/lang/default/").toURI());

                for (File file : defaultLang.listFiles()) {
                    if (file.getName().endsWith(".json")) {
                        JsonElement root = JsonParser.parseReader(new FileReader(file));

                        for (Map.Entry<String, JsonElement> e : root.getAsJsonObject().entrySet()) {
                            provider.add(e.getKey(), e.getValue().getAsString());
                        }
                    }
                }
            } catch (FileNotFoundException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });

        REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, provider -> {
            for (CNATags.Block tag : CNATags.Block.values()) {
                if (tag.hasItemTag) {
                    provider.copy(tag.blockTag, tag.itemTag);
                }
            }
        });

        REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, CNATags.Block::generate);
        REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, CNATags.Item::generate);

        providerConsumer.addProvider(CNAStandardRecipeGen::new);
        providerConsumer.addProvider(CNAProcessingRecipeGen::new);
        providerConsumer.addProvider(CNAMechanicalCraftingRecipeGen::new);
        providerConsumer.addProvider(CNASequencedAssemblyRecipeGen::new);
        providerConsumer.addProvider(CNAGeneratedEntriesProvider::new);
        providerConsumer.addProvider(CNASounds.SoundEntryProvider::new);
    }
}
