package org.antarcticgardens.cna.content.motor.variants;

import org.antarcticgardens.cna.config.CNAConfig;

public class BasicMotorVariant implements IMotorVariant {
    @Override
    public long getMaxCapacity() {
        return CNAConfig.getServer().basicMotorCapacity.get();
    }

    @Override
    public float getSpeed() {
        return CNAConfig.getServer().basicMotorSpeed.get().floatValue();
    }

    @Override
    public float getStress() {
        return CNAConfig.getServer().basicMotorStress.get().floatValue();
    }
}
