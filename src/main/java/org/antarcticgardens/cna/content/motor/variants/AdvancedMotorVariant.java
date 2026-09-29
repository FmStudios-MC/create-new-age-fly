package org.antarcticgardens.cna.content.motor.variants;

import org.antarcticgardens.cna.config.CNAConfig;

public class AdvancedMotorVariant implements IMotorVariant {
    @Override
    public long getMaxCapacity() {
        return CNAConfig.getServer().advancedMotorCapacity.get();
    }

    @Override
    public float getSpeed() {
        return CNAConfig.getServer().advancedMotorSpeed.get().floatValue();
    }

    @Override
    public float getStress() {
        return CNAConfig.getServer().advancedMotorStress.get().floatValue();
    }
}
