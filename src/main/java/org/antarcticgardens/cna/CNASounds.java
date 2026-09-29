package org.antarcticgardens.cna;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

/**
 * The sound definitions themselves are in the committed {@code assets/create_new_age/sounds.json},
 * which Create's sound entry builder used to generate.
 */
public class CNASounds {
    public static final SoundEvent GEIGER_COUNTER = register("geiger_counter");

    static void init() {
    }

    public static void playOnServer(SoundEvent event, Level level, BlockPos pos, float volume, float pitch) {
        level.playSound(null, pos, event, SoundSource.PLAYERS, volume, pitch);
    }

    private static SoundEvent register(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(CreateNewAge.MOD_ID, name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }
}
