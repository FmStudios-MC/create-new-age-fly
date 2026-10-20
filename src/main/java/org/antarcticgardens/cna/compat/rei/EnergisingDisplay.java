package org.antarcticgardens.cna.compat.rei;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.zurrtum.create.compat.rei.IngredientHelper;
import com.zurrtum.create.content.processing.recipe.ProcessingOutput;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.antarcticgardens.cna.content.energising.recipe.EnergisingRecipe;

import java.util.List;
import java.util.Optional;

/** An energising recipe as REI shows it; built on the server and synced to clients by REI. */
public record EnergisingDisplay(EntryIngredient input, List<ProcessingOutput> outputs, int energyNeeded,
                                Optional<ResourceLocation> location) implements Display {
    public static final DisplaySerializer<EnergisingDisplay> SERIALIZER = DisplaySerializer.of(
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    EntryIngredient.codec().fieldOf("input").forGetter(EnergisingDisplay::input),
                    ProcessingOutput.CODEC.listOf().fieldOf("outputs").forGetter(EnergisingDisplay::outputs),
                    Codec.INT.fieldOf("energy_needed").forGetter(EnergisingDisplay::energyNeeded),
                    ResourceLocation.CODEC.optionalFieldOf("location").forGetter(EnergisingDisplay::location)
            ).apply(instance, EnergisingDisplay::new)),
            StreamCodec.composite(
                    EntryIngredient.streamCodec(), EnergisingDisplay::input,
                    ProcessingOutput.STREAM_CODEC.apply(ByteBufCodecs.list()), EnergisingDisplay::outputs,
                    ByteBufCodecs.VAR_INT, EnergisingDisplay::energyNeeded,
                    ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), EnergisingDisplay::location,
                    EnergisingDisplay::new
            )
    );

    public EnergisingDisplay(RecipeHolder<EnergisingRecipe> entry) {
        this(IngredientHelper.getInputEntryIngredient(entry.value().ingredient()), entry.value().results(),
                entry.value().getEnergyNeeded(), Optional.of(entry.id().location()));
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        return List.of(input);
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return outputs.stream().map(output -> EntryIngredients.of(output.create())).toList();
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return CNAReiCommonPlugin.ENERGISING;
    }

    @Override
    public Optional<ResourceLocation> getDisplayLocation() {
        return location;
    }

    @Override
    public DisplaySerializer<? extends Display> getSerializer() {
        return SERIALIZER;
    }
}
