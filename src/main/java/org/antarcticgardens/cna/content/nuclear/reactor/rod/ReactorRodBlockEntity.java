package org.antarcticgardens.cna.content.nuclear.reactor.rod;

import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.world.level.storage.ValueOutput;

import net.minecraft.world.level.storage.ValueInput;

import com.zurrtum.create.client.api.goggles.IHaveGoggleInformation;
import com.zurrtum.create.client.foundation.utility.CreateLang;
import com.zurrtum.create.client.catnip.lang.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.antarcticgardens.cna.config.CNAConfig;
import org.antarcticgardens.cna.content.heat.HeatBlockEntity;
import org.antarcticgardens.cna.content.nuclear.NuclearUtil;
import org.antarcticgardens.cna.util.StringFormatUtil;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ReactorRodBlockEntity extends SmartBlockEntity implements HeatBlockEntity {

    public static final int MAX_FUEL = 172800;

    public ReactorRodBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        working = blockState.getValue(ReactorRodBlock.ACTIVE);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour<?>> behaviours) {
    }

    int twoSeconds = 0;
    private boolean working;
    public void tick(BlockPos pos, Level world, BlockState state) {
        var common = CNAConfig.getServer();
        double multiplier = common.overheatingMultiplier.get();
        last = common.nuclearReactorRodHeat.get();
        if (fuel <= 0) {
            last = 0;
        }
        if (multiplier > 0 && this.heat > 16000*multiplier) {
            last-= common.nuclearReactorRodHeatLoss.get();
            setChanged();
            float explosionRadius = CNAConfig.getServer().radiationDamageExplosionScale.get().floatValue() * CNAConfig.getServer().reactorOverheatExplosionMultiplier.get().floatValue();
            HeatBlockEntity.handleOverheat(this, () -> ((ReactorRodBlock) state.getBlock()).explode(level, pos, state, explosionRadius,
                    CNAConfig.getServer().radiationDamageExplosionFire.get() && CNAConfig.getServer().reactorOverheatExplosionMultiplier.get().floatValue() > 0));
        }
        twoSeconds++;
        if (twoSeconds > 40) {
            transferAroundRodOnly(this);
            working = state.getValue(ReactorRodBlock.ACTIVE);

            twoSeconds = 0;
            if (working) {
                NuclearUtil.createRadiation(8, world, pos);
            }
        }
        if (fuel > 0) {
            fuel--;
            if (!working) {
                world.setBlock(pos, state.setValue(ReactorRodBlock.ACTIVE, true), 3);
                working = true;
            }
            heat+= (float) last;
            setChanged();
        } else {
            if (working) {
                world.setBlock(pos, state.setValue(ReactorRodBlock.ACTIVE, false), 3);
                working = false;
                setChanged();
            }
        }
    }


    static <T extends  BlockEntity & HeatBlockEntity> void transferAroundRodOnly(T self) {
        if (self.getLevel() == null) {
            return;
        }
        float totalToAverage = self.getHeat();
        int totalBlocks = 1;
        HeatBlockEntity[] setters = new HeatBlockEntity[6];
        for (int i = 0 ; i < 6 ; i++) {
            Direction value = Direction.values()[i];
            BlockEntity entity = self.getLevel().getBlockEntity(self.getBlockPos().relative(value));
            if (entity instanceof ReactorRodBlockEntity hbe && hbe.canAdd(value)) {
                setters[i] = hbe;
                totalToAverage += hbe.getHeat();
                totalBlocks++;
            }
        }
        HeatBlockEntity.average(self, totalToAverage, totalBlocks, setters);
    }

    @Override
    public float maxHeat() {
        return 26000;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    public float heat = 0;
    public int fuel = 0;
    public double last = 0;

    @Override
    protected void read(ValueInput tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        heat = tag.getFloatOr("heat", 0f);
        fuel = tag.getIntOr("fuel", 0);
        last = tag.getDoubleOr("last", 0d);
    }

    @Override
    protected void write(ValueOutput tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putFloat("heat", heat);
        tag.putInt("fuel", fuel);
        tag.putDouble("last", last);
    }


    @Override
    public float getHeat() {
        return heat;
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
}
