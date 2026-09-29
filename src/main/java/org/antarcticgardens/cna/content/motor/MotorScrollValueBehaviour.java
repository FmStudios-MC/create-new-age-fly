package org.antarcticgardens.cna.content.motor;

import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.foundation.blockEntity.behaviour.scrollValue.ServerKineticScrollValueBehaviour;

/**
 * Server half of the motor's speed setting. Create Fly's kinetic scroll value already maps the two
 * board rows to the two rotation directions, exactly as upstream's copy did. The value box and
 * board are in {@code client.behaviour.MotorScrollBehaviour}.
 */
public class MotorScrollValueBehaviour extends ServerKineticScrollValueBehaviour {
    public MotorScrollValueBehaviour(SmartBlockEntity be) {
        super(be);
    }

    /** Sets the value without clamping or the callback, as upstream wrote the field directly. */
    public void setRawValue(int value) {
        this.value = value;
    }

    public void betweenValidated(int min, int max) {
        this.between(min, max);

        if (value > max) {
            value = max;
        } else if (value < min) {
            value = min;
        }
    }
}
