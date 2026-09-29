package org.antarcticgardens.cna.content.motor.extension.variants;

import org.antarcticgardens.cna.config.CNAConfig;

public class BasicMotorExtensionVariant implements IMotorExtensionVariant {
    @Override
    public float getMultiplier() {
        return CNAConfig.getServer().basicMotorExtensionMultiplier.get().floatValue();
    }

    @Override
    public long getExtraCapacity() {
        return CNAConfig.getServer().basicMotorExtensionExtraCapacity.get();
    }

    @Override
    public int getScrollStep() {
        return CNAConfig.getServer().basicMotorExtensionScrollStep.get();
    }
}
