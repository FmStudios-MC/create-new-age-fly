package org.antarcticgardens.cna.content.heat.stirling;

import net.minecraft.world.level.storage.ValueOutput;

import net.minecraft.world.level.storage.ValueInput;

import com.zurrtum.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.zurrtum.create.catnip.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.antarcticgardens.cna.content.heat.HeatBlockEntity;


public class StirlingEngineBlockEntity extends GeneratingKineticBlockEntity implements HeatBlockEntity {

    LerpedFloat visualSpeed = LerpedFloat.linear();
    float angle;
    private float heat = 0;

    public StirlingEngineBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    protected void write(ValueOutput compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putFloat("heat", heat);
        compound.putFloat("gSpeed", speed);
    }

    @Override
    public float getTierHeat() {
        return speed * 3.125f;
    }
    
    @Override
    public float[] getHeatTiers() {
        return new float[] {
                50,
                100
        };
    }


    @Override
    protected void read(ValueInput compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        if (clientPacket)
            visualSpeed.chase(getGeneratedSpeed(), 1 / 64f, LerpedFloat.Chaser.EXP);

        heat = compound.getFloatOr("heat", 0f);
        speed = compound.getFloatOr("gSpeed", 0f);
    }

    public float speed = 0;

    @Override
    public float getGeneratedSpeed() {
        return speed;
    }

    @Override
    public void tick() {
        super.tick();
        if (getLevel() == null)
            return;

        if (getLevel().getGameTime() % 20 == 0) {
            HeatBlockEntity.transferAround(this);
            if (getHeat() > 50) {
                if (getHeat() > 100) {
                    setHeat(getHeat() - 100);
                    if (speed != 32) {
                        speed = 32;
                        updateGeneratedRotation();
                    }
                    HeatBlockEntity.handleOverheat(this);
                } else {
                    setHeat(getHeat() - 50);
                    if (speed != 16) {
                        speed = 16;
                        updateGeneratedRotation();
                    }
                }
            } else {
                if (speed != 0) {
                    speed = 0;
                    updateGeneratedRotation();
                }
            }
        }

        if (!getLevel().isClientSide()) {
            HeatBlockEntity.trySync(this);
            return;
        }

        float targetSpeed = getSpeed();
        visualSpeed.updateChaseTarget(targetSpeed);
        visualSpeed.tickChaser();
        angle += visualSpeed.getValue() * 3 / 10f;
        angle %= 360;
    }

    @Override
    public float getHeat() {
        return heat;
    }

    @Override
    public boolean canConnect(Direction from) {
        return from == Direction.UP;
    }

    @Override
    public void addHeat(float amount) {
        heat += amount;
        setChanged();
    }

    @Override
    public void setHeat(float amount) {
        heat = amount;
        setChanged();
    }

    @Override
    public float calculateAddedStressCapacity() {
        float impact = 32.0f;
        this.lastStressApplied = impact;
        return impact;
    }}
