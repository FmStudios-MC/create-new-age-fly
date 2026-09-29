package org.antarcticgardens.cna.content.nuclear;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import org.antarcticgardens.cna.CNASounds;
import org.antarcticgardens.cna.config.CNAConfig;

public class RadiationPoisoningEffect extends MobEffect {
    public RadiationPoisoningEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity livingEntity, int amplifier) {
        // 26.2 only ticks effects on the server, so upstream's isClientSide() guard is implied.
        {
            if (CNAConfig.getServer().geigerCounterSounds.get()) {
                float clickChance = 0.15f + (amplifier * 0.10f);
                if (livingEntity.getRandom().nextFloat() < clickChance && livingEntity instanceof Player) {
                    float pitch = 0.95f + livingEntity.getRandom().nextFloat() * 0.1f;
                    CNASounds.playOnServer(CNASounds.GEIGER_COUNTER, livingEntity.level(), livingEntity.blockPosition(), .75f, pitch);
                }
            }
            if (CNAConfig.getServer().nauseaInducingRadiation.get()) {
                MobEffectInstance nausea = livingEntity.getEffect(MobEffects.NAUSEA);
                if (nausea == null || nausea.getDuration() < 100) {
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 400, amplifier, false, false, false));
                }
            }

            MobEffectInstance fatigue = livingEntity.getEffect(MobEffects.MINING_FATIGUE);
            if (fatigue == null || fatigue.getDuration() < 100) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.MINING_FATIGUE, 300, amplifier + 1, false, false, false));
            }

            if (livingEntity.tickCount % 40 == 0) {
                livingEntity.hurtServer(level, livingEntity.damageSources().magic(), 1.0F + amplifier);
            }
        }

        return super.applyEffectTick(level, livingEntity, amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
