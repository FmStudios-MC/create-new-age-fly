package org.antarcticgardens.cna.content.motor.extension;

import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.foundation.blockEntity.behaviour.ValueSettings;
import com.zurrtum.create.foundation.blockEntity.behaviour.scrollValue.ServerScrollValueBehaviour;
import net.minecraft.world.entity.player.Player;

/**
 * Server half of the motor extension's stress multiplier, in percent. The board works in units of
 * {@link #step}, so board value {@code n} means {@code n * step} percent. The value box and board are
 * in {@code client.behaviour.MotorExtensionScrollBehaviour}.
 */
public class MotorExtensionScrollValueBehaviour extends ServerScrollValueBehaviour {
    protected int step;

    public MotorExtensionScrollValueBehaviour(SmartBlockEntity be, int step) {
        super(be);
        this.step = step;
    }

    public int getStep() {
        return step;
    }

    /** Sets the value before a range exists, where {@link #setValue} would clamp it to [0, 1]. */
    public void setInitialValue(int value) {
        this.value = value;
    }

    @Override
    public void setValueSettings(Player player, ValueSettings valueSetting, boolean ctrlHeld) {
        int value = Math.max(1, valueSetting.value());
        if (!valueSetting.equals(getValueSettings()))
            playFeedbackSound(this);
        setValue(value * step);
    }

    // Upstream divided the value by the step inside createBoard on the client; the server half
    // hands out board units directly instead.
    @Override
    public ValueSettings getValueSettings() {
        return new ValueSettings(0, Math.abs(value) / step);
    }

    public void betweenValidated(int min, int max) {
        this.between(min, max);

        if (value > max) {
            value = max;
        } else if (value < min) {
            value = min;
        }
    }

    @Override
    public String getClipboardKey() {
        return "Stress Multiplier";
    }
}
