package org.antarcticgardens.cna.content.motor.extension.variants;

import org.antarcticgardens.cna.config.CNAConfig;

public class AdvancedMotorExtensionVariant implements IMotorExtensionVariant {
    @Override
    public float getMultiplier() {
        return CNAConfig.getServer().advancedMotorExtensionMultiplier.get().floatValue();
    }

    @Override
    public long getExtraCapacity() {
        return CNAConfig.getServer().advancedMotorExtensionExtraCapacity.get();
    }

    @Override
    public int getScrollStep() {
        return CNAConfig.getServer().advancedMotorExtensionScrollStep.get();
    }
}
