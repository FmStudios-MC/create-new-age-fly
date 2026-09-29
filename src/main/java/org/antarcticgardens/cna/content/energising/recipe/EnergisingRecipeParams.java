package org.antarcticgardens.cna.content.energising.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public class EnergisingRecipeParams extends ProcessingRecipeParams {
    public static MapCodec<EnergisingRecipeParams> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            codec(EnergisingRecipeParams::new).forGetter(Function.identity()),
            Codec.INT.optionalFieldOf("energy_needed", 0).forGetter(EnergisingRecipeParams::energyNeeded)
    ).apply(instance, (params, energyNeeded) -> {
        params.energyNeeded = energyNeeded;
        return params;
    }));
    public static StreamCodec<RegistryFriendlyByteBuf, EnergisingRecipeParams> STREAM_CODEC = streamCodec(EnergisingRecipeParams::new);

    protected int energyNeeded;

    protected final int energyNeeded() {
        return energyNeeded;
    }

    @Override
    protected void encode(RegistryFriendlyByteBuf buffer) {
        super.encode(buffer);
        ByteBufCodecs.INT.encode(buffer, energyNeeded);
    }

    @Override
    protected void decode(RegistryFriendlyByteBuf buffer) {
        super.decode(buffer);
        energyNeeded = ByteBufCodecs.INT.decode(buffer);
    }
}
