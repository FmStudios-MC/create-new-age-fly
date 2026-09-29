package org.antarcticgardens.cna.content.nuclear;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.antarcticgardens.cna.CNASounds;
import org.antarcticgardens.cna.config.CNAConfig;

public class RadiationPoisoningEffect extends MobEffect {
    public RadiationPoisoningEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide) {
            if (CNAConfig.getServer().geigerCounterSounds.get()) {
                float clickChance = 0.15f + (amplifier * 0.10f);
                if (livingEntity.getRandom().nextFloat() < clickChance && livingEntity.getType() == EntityType.PLAYER) {
                    float pitch = 0.95f + livingEntity.getRandom().nextFloat() * 0.1f;
                    CNASounds.GEIGER_COUNTER.playOnServer(livingEntity.level(), livingEntity.blockPosition(), .75f, pitch);
                }
            }
            if (CNAConfig.getServer().nauseaInducingRadiation.get()) {
                MobEffectInstance nausea = livingEntity.getEffect(MobEffects.CONFUSION);
                if (nausea == null || nausea.getDuration() < 100) {
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 400, amplifier, false, false, false));
                }
            }

            MobEffectInstance fatigue = livingEntity.getEffect(MobEffects.DIG_SLOWDOWN);
            if (fatigue == null || fatigue.getDuration() < 100) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 300, amplifier + 1, false, false, false));
            }

            if (livingEntity.tickCount % 40 == 0) {
                livingEntity.hurt(livingEntity.damageSources().magic(), 1.0F + amplifier);
            }
        }

        return super.applyEffectTick(livingEntity, amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
