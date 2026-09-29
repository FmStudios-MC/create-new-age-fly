package org.antarcticgardens.cna.content.electricity.light;

import net.minecraft.world.level.storage.ValueOutput;

import net.minecraft.world.level.storage.ValueInput;

import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.foundation.blockEntity.behaviour.scrollValue.ServerScrollValueBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.antarcticgardens.cna.CNABlockEntityTypes;
import org.antarcticgardens.cna.config.CNAConfig;
import org.antarcticgardens.cna.content.electricity.connector.AbstractElectricalConnector;
import org.antarcticgardens.cna.content.electricity.network.ElectricalNetwork;
import org.antarcticgardens.cna.content.electricity.network.SimpleNetworkEnergyStorage;
import org.antarcticgardens.cna.util.RunnableUtil;
import team.reborn.energy.api.EnergyStorage;

import java.util.List;

public class StreetLightBlockEntity extends AbstractElectricalConnector {
    private final SimpleNetworkEnergyStorage storage;

    private long prvEnergy = -100000;
    public ServerScrollValueBehaviour lightLevelBehaviour;

    public StreetLightBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);

        storage = new SimpleNetworkEnergyStorage(this, null, CNAConfig.getServer().streetLightCapacity.get())
                .onFinalCommit(RunnableUtil.createBlockEntityUpdater(this))
                .setSupportsExtraction(false);

    }

    @Override
    protected void read(ValueInput tag, boolean clientPacket) {
        storage.setStoredEnergy(tag.getLongOr("energy", 0L));
        super.read(tag, clientPacket);
    }

    @Override
    public Direction getFacing() {
        return null;
    }

    @Override
    protected void write(ValueOutput tag, boolean clientPacket) {
        tag.putLong("energy", storage.getAmount());
        super.write(tag, clientPacket);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour<?>> behaviours) {
        lightLevelBehaviour = new ServerScrollValueBehaviour(this).between(0, 15);
        lightLevelBehaviour.setValue(15);
        lightLevelBehaviour.withCallback( i -> {
            if (getLevel() != null && storage.getAmount() > 0)
                getLevel().setBlock(getBlockPos(), getBlockState().setValue(StreetLightBlock.LIGHT_LEVEL, i), 3);
        });
        behaviours.add(lightLevelBehaviour);
        super.addBehaviours(behaviours);
    }


    @Override
    protected void serverTick() {
        super.serverTick();
        if (getLevel() == null)
            return;
        long needed = (long) lightLevelBehaviour.getValue() * CNAConfig.getServer().streetLightLevelExtraction.get();
        long e = storage.internalExtract(needed, false);
        if (prvEnergy == storage.getAmount())
            return;
        if (e <= 0) {
            getLevel().setBlock(getBlockPos(), getBlockState().setValue(StreetLightBlock.LIGHT_LEVEL, 0), 3);
        } else {
            getLevel().setBlock(getBlockPos(), getBlockState().setValue(StreetLightBlock.LIGHT_LEVEL, lightLevelBehaviour.getValue()), 3);
        }
        if (storage.getAmount() != prvEnergy && level.getGameTime() % 20 == 0) {
            this.sendData();
            prvEnergy = storage.getAmount();
        }
    }


    @Override
    public void setNetwork(ElectricalNetwork network) {
        super.setNetwork(network);
        storage.setNetwork(network);
    }

    public Vec3 getConnectionPoint() {
        return new Vec3(0.5f, 1/16f, 0.5f);
    }
    /** Exposes the storage to Team Reborn Energy. Was re-registered from every constructor under ESL. */
    public static void registerEnergyStorage() {
        EnergyStorage.SIDED.registerForBlockEntities((blockEntity, direction) -> ((StreetLightBlockEntity) blockEntity).storage, CNABlockEntityTypes.STREET_LIGHT);
    }
    public SimpleNetworkEnergyStorage getEnergyStorage() {
        return storage;
    }
}
