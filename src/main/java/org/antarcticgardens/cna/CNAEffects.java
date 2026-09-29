package org.antarcticgardens.cna;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import org.antarcticgardens.cna.content.nuclear.RadiationPoisoningEffect;

public class CNAEffects {
    public static final Holder<MobEffect> RADIATION_POISONING = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,
            Identifier.fromNamespaceAndPath(CreateNewAge.MOD_ID, "radiation_poisoning"),
            new RadiationPoisoningEffect(MobEffectCategory.HARMFUL, 0x48F542));

    static void init() {
    }
}
