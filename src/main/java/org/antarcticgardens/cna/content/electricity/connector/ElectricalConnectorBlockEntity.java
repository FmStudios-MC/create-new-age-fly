package org.antarcticgardens.cna.content.electricity.connector;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.antarcticgardens.cna.CNABlockEntityTypes;
import org.antarcticgardens.cna.content.electricity.network.ElectricalNetwork;
import org.antarcticgardens.cna.content.electricity.network.NetworkEnergyStorage;
import team.reborn.energy.api.EnergyStorage;

import java.util.*;

public class ElectricalConnectorBlockEntity extends AbstractElectricalConnector {
    private final NetworkEnergyStorage storage;

    public ElectricalConnectorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        storage = new NetworkEnergyStorage(this, null);
    }


    @Override
    public BlockPos getSupportingBlockPos() {
        return getBlockPos().relative(getBlockState().getValue(BlockStateProperties.FACING).getOpposite());
    }

    @Override
    public void setNetwork(ElectricalNetwork network) {
        super.setNetwork(network);
        storage.setNetwork(network);
    }

    @Override
    public Direction getFacing() {
        return getBlockState().getValue(BlockStateProperties.FACING);
    }
    /** Exposes the storage to Team Reborn Energy. Was re-registered from every constructor under ESL. */
    public static void registerEnergyStorage() {
        EnergyStorage.SIDED.registerForBlockEntities((blockEntity, direction) -> ((ElectricalConnectorBlockEntity) blockEntity).storage, CNABlockEntityTypes.ELECTRICAL_CONNECTOR);
    }
}
