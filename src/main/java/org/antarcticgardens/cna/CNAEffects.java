package org.antarcticgardens.cna;

import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import org.antarcticgardens.cna.content.nuclear.RadiationPoisoningEffect;

import static org.antarcticgardens.cna.CreateNewAge.REGISTRATE;

public class CNAEffects {
    public static final RegistryEntry<MobEffect, RadiationPoisoningEffect> RADIATION_POISONING =
            REGISTRATE.object("radiation_poisoning")
                    .simple(Registries.MOB_EFFECT, () -> new RadiationPoisoningEffect(MobEffectCategory.HARMFUL, 0x48F542));

    public static void load() {}
}

