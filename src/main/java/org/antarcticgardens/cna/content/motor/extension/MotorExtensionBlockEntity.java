package org.antarcticgardens.cna.content.motor.extension;

import net.minecraft.world.level.storage.ValueOutput;

import net.minecraft.world.level.storage.ValueInput;

import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import org.antarcticgardens.cna.CNABlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.antarcticgardens.cna.content.motor.extension.variants.IMotorExtensionVariant;

import java.util.List;

public class MotorExtensionBlockEntity extends SmartBlockEntity {
    private MotorExtensionScrollValueBehaviour stressBehavior;
    private float multiplier = 1;
    private final IMotorExtensionVariant variant;

    public MotorExtensionBlockEntity(BlockEntityType<?> arg, BlockPos arg2, BlockState arg3, IMotorExtensionVariant variant) {
        super(arg, arg2, arg3);
        this.variant = variant;
    }

    public static CNABlockEntityTypes.Factory<MotorExtensionBlockEntity> create(IMotorExtensionVariant variant) {
        return (type, pos, state) -> new MotorExtensionBlockEntity(type, pos, state, variant);
    }

    @Override
    public void tick() {
        super.tick();
        
        if (multiplier > variant.getMultiplier()) {
            multiplier = variant.getMultiplier();
            stressBehavior.setValue((int) (multiplier * 100));
        }
        
        stressBehavior.step = variant.getScrollStep();
        stressBehavior.betweenValidated(1, (int)(100 * variant.getMultiplier()));
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour<?>> behaviours) {
        stressBehavior = new MotorExtensionScrollValueBehaviour(this, 1);
        stressBehavior.setRawValue(100);
        stressBehavior.withCallback(i -> {
            multiplier = i/100f;
            this.notifyUpdate();
        });
        behaviours.add(stressBehavior);
    }
    
    public float getMultiplier() {
        return multiplier;
    }
    
    public IMotorExtensionVariant getVariant() {
        return variant;
    }


    @Override
    protected void read(ValueInput tag, boolean clientPacket) {
        multiplier = tag.getFloatOr("stressMultiplier", 0f);
        stressBehavior.setRawValue((int) multiplier * 100);
        super.read(tag, clientPacket);
    }

    @Override
    protected void write(ValueOutput tag, boolean clientPacket) {
        tag.putFloat("stressMultiplier", multiplier);
        super.write(tag, clientPacket);
    }
}
