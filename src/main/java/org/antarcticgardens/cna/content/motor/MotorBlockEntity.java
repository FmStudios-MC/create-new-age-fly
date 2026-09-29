package org.antarcticgardens.cna.content.motor;

import net.minecraft.world.level.storage.ValueOutput;

import net.minecraft.world.level.storage.ValueInput;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.client.api.goggles.IHaveGoggleInformation;
import com.zurrtum.create.content.kinetics.KineticNetwork;
import com.zurrtum.create.content.kinetics.base.DirectionalKineticBlock;
import com.zurrtum.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.zurrtum.create.content.kinetics.base.IRotate;
import com.zurrtum.create.content.kinetics.motor.CreativeMotorBlock;
import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.zurrtum.create.client.foundation.utility.CreateLang;
import org.antarcticgardens.cna.CNABlockEntityTypes;
import com.zurrtum.create.client.flywheel.lib.transform.TransformStack;
import com.zurrtum.create.catnip.math.AngleHelper;
import com.zurrtum.create.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.antarcticgardens.cna.CNABlockEntityTypes;
import org.antarcticgardens.cna.config.CNAConfig;
import org.antarcticgardens.cna.content.motor.extension.MotorExtensionBlockEntity;
import org.antarcticgardens.cna.content.motor.variants.AdvancedMotorVariant;
import org.antarcticgardens.cna.content.motor.variants.BasicMotorVariant;
import org.antarcticgardens.cna.content.motor.variants.IMotorVariant;
import org.antarcticgardens.cna.content.motor.variants.ReinforcedMotorVariant;
import org.antarcticgardens.cna.util.RunnableUtil;
import org.antarcticgardens.cna.util.StringFormatUtil;
import team.reborn.energy.api.EnergyStorage;
import org.antarcticgardens.cna.energy.SimpleEnergyStorage;

import java.util.List;

public class MotorBlockEntity extends GeneratingKineticBlockEntity {
    private final SimpleEnergyStorage storage;
    
    public boolean needsPower = false;
    private final IMotorVariant variant;
    public final int tier;
    public MotorScrollValueBehaviour speedBehavior;
    public boolean powered = false;
    private float actualSpeed = 0;
    private float actualStress = 0;
    private long prvEnergy = -100000;


    private float speed = 0;
    private float stress = 0;

    public MotorBlockEntity(BlockEntityType<?> arg, BlockPos arg2, BlockState arg3, IMotorVariant variant) {
        super(arg, arg2, arg3);
        this.variant = variant;
        switch (variant) {
            case BasicMotorVariant basicMotorVariant -> this.tier = 1;
            case AdvancedMotorVariant advancedMotorVariant -> this.tier = 2;
            case ReinforcedMotorVariant reinforcedMotorVariant -> this.tier = 3;
            case null, default -> this.tier = 0;
        }

        storage = new SimpleEnergyStorage(variant.getMaxCapacity())
                .onFinalCommit(RunnableUtil.createBlockEntityUpdater(this))
                .setSupportsExtraction(false);

    }

    public static CNABlockEntityTypes.Factory<MotorBlockEntity> create(IMotorVariant variant) {
        return (type, pos, state) -> new MotorBlockEntity(type, pos, state, variant);
    }

    @Override
    public void initialize() {
        powered = getLevel().hasNeighborSignal(this.getBlockPos());
        super.initialize();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour<?>> behaviours) {
        super.addBehaviours(behaviours);
        speedBehavior = new MotorScrollValueBehaviour(this);
        speedBehavior.setInitialValue(getDefaultSpeed());
        speedBehavior.withCallback(i -> this.updateGeneratedRotation());
        behaviours.add(speedBehavior);
    }

    @Override
    public void invalidate() {
        super.invalidate();
    }



    public int getDefaultSpeed() {
        return 16;
    }

    @Override
    protected void read(ValueInput compound, boolean clientPacket) {
        storage.setStoredEnergy(compound.getLongOr("energy", 0L));
        actualSpeed = compound.getFloatOr("aSpeed", 0f);
        needsPower = compound.getBooleanOr("needsPower", false);
        stress = compound.getFloatOr("lastGeneratedStress", 0f);
        speed = compound.getFloatOr("lastGeneratedSpeed", 0f);
        e = compound.getLongOr("eUse", 0L);
        actualStress = compound.getFloatOr("actualStress", 0f);
        super.read(compound, clientPacket);
    }

    @Override
    protected void write(ValueOutput compound, boolean clientPacket) {
        compound.putLong("energy", storage.getStoredEnergy());
        compound.putFloat("aSpeed", actualSpeed);
        compound.putBoolean("needsPower", needsPower);
        compound.putFloat("lastGeneratedStress", stress);
        compound.putFloat("lastGeneratedSpeed", speed);
        compound.putFloat("eUse", e);
        compound.putFloat("actualStress", actualStress);
        super.write(compound, clientPacket);
    }

    @Override
    public float calculateStressApplied() {
        return 0;
    }

    private long e;


    @Override
    public float calculateAddedStressCapacity() {
        if (level != null && level.isClientSide()) {
            return this.lastCapacityProvided;
        }

        this.lastCapacityProvided = actualStress / actualSpeed;
        this.lastCapacityProvided = Float.isNaN(this.lastCapacityProvided) || Float.isInfinite(this.lastCapacityProvided) ? 0 : Math.abs(this.lastCapacityProvided);
        return this.lastCapacityProvided;
    }

    @Override
    public float getGeneratedSpeed() {
        return actualSpeed;
    }

    public void updateGeneratedRotation() {
        float speed = getGeneratedSpeed();
        float prevSpeed = this.speed;

        if (level == null || level.isClientSide())
            return;

        if (prevSpeed != speed) {
            if (!hasSource()) {
                IRotate.SpeedLevel levelBefore = IRotate.SpeedLevel.of(this.speed);
                IRotate.SpeedLevel levelafter = IRotate.SpeedLevel.of(speed);
                if (levelBefore != levelafter)
                    effects.queueRotationIndicators();
            }

            applyNewSpeed(prevSpeed, speed);
        }

        if (hasNetwork() && speed != 0) {
            KineticNetwork network = getOrCreateNetwork();
            network.updateCapacityFor(this, stress);
            notifyStressCapacityChange(calculateAddedStressCapacity());
            getOrCreateNetwork().updateStressFor(this, calculateStressApplied());
            network.updateStress();
        }

        onSpeedChanged(prevSpeed);

        sendData();
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null)
            return;

        float stressMultiplier = 1;
        long extraEnergy = 0;

        Direction dir = this.getBlockState().getValue(DirectionalKineticBlock.FACING);
        if (level.getBlockState(getBlockPos().relative(dir.getOpposite())).getOptionalValue(DirectionalKineticBlock.FACING).orElse(dir.getOpposite()) == dir
                && level.getBlockEntity(getBlockPos().relative(dir.getOpposite()))
                instanceof MotorExtensionBlockEntity extension) {
            stressMultiplier = extension.getMultiplier();
            extraEnergy = extension.getVariant().getExtraCapacity();
        }
        
        storage.setCapacity(extraEnergy + variant.getMaxCapacity());
        speedBehavior.betweenValidated((int) -variant.getSpeed(), (int) variant.getSpeed());

        if (!level.isClientSide()) {
            int needed = (int) Math.ceil((variant.getStress() * stressMultiplier
                        * CNAConfig.getServer().motorSUMultiplier.get())
                    * CNAConfig.getServer().suToEnergy.get());
            e = needsPower == powered ? storage.internalExtract(needed, false) : 0;
            if (e > 0) {
                actualSpeed = speedBehavior.getValue();
                actualStress =
                        (float) Math.ceil((variant.getStress() * stressMultiplier
                                    * CNAConfig.getServer().motorSUMultiplier.get())
                                * (e / (float)needed));
            } else {
                actualSpeed = 0;
                actualStress = 0;
            }
            if (((actualSpeed != speed) || (actualStress != stress))) {
                updateGeneratedRotation();
                speed = actualSpeed;
                stress = actualStress;
            } else if (storage.getStoredEnergy() != prvEnergy && level.getGameTime() % 20 == 0) {
                this.sendData();
                prvEnergy = storage.getStoredEnergy();
            }
        }
    }
    /** Exposes the storage to Team Reborn Energy. Was re-registered from every constructor under ESL. */
    public static void registerEnergyStorage() {
        EnergyStorage.SIDED.registerForBlockEntities((blockEntity, direction) -> ((MotorBlockEntity) blockEntity).storage, CNABlockEntityTypes.BASIC_MOTOR);
        EnergyStorage.SIDED.registerForBlockEntities((blockEntity, direction) -> ((MotorBlockEntity) blockEntity).storage, CNABlockEntityTypes.ADVANCED_MOTOR);
        EnergyStorage.SIDED.registerForBlockEntities((blockEntity, direction) -> ((MotorBlockEntity) blockEntity).storage, CNABlockEntityTypes.REINFORCED_MOTOR);
    }
    public SimpleEnergyStorage getEnergyStorage() {
        return storage;
    }

    public long getLastConsumed() {
        return e;
    }
}
