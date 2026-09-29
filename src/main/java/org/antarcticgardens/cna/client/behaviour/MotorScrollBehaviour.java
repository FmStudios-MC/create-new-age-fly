package org.antarcticgardens.cna.client.behaviour;

import com.google.common.collect.ImmutableList;
import com.zurrtum.create.client.content.kinetics.motor.MotorValueBox;
import com.zurrtum.create.client.foundation.blockEntity.ValueSettingsBoard;
import com.zurrtum.create.client.foundation.blockEntity.ValueSettingsFormatter;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.scrollValue.KineticScrollValueBehaviour;
import com.zurrtum.create.client.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import org.antarcticgardens.cna.content.motor.MotorBlockEntity;

/**
 * Client half of the motor's speed setting. Upstream's value box was a copy of the creative
 * motor's, so Create Fly's {@link MotorValueBox} is used as is. The board is sized to the motor
 * tier's top speed rather than the creative motor's fixed 256.
 */
public class MotorScrollBehaviour extends KineticScrollValueBehaviour {
    public MotorScrollBehaviour(MotorBlockEntity be) {
        super(CreateLang.translateDirect("kinetics.creative_motor.rotation_speed"), be, new MotorValueBox());
        needsWrench = true;
    }

    @Override
    public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
        ImmutableList<Component> rows = ImmutableList.of(
                Component.literal("⟳").withStyle(ChatFormatting.BOLD),
                Component.literal("⟲").withStyle(ChatFormatting.BOLD));
        ValueSettingsFormatter formatter = new ValueSettingsFormatter(this::formatSettings);
        int max = behaviour.getMax();
        return new ValueSettingsBoard(label, max, max / 8, rows, formatter);
    }
}
