package org.antarcticgardens.cna.content.electricity.generation.brushes;

import net.minecraft.world.level.storage.ValueOutput;

import net.minecraft.world.level.storage.ValueInput;

import com.zurrtum.create.client.api.goggles.IHaveGoggleInformation;
import com.zurrtum.create.content.kinetics.base.DirectionalKineticBlock;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.client.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.antarcticgardens.cna.CNABlockEntityTypes;
import org.antarcticgardens.cna.config.CNAConfig;
import org.antarcticgardens.cna.content.electricity.generation.coil.GeneratorCoilBlock;
import org.antarcticgardens.cna.content.electricity.generation.coil.GeneratorCoilBlockEntity;
import org.antarcticgardens.cna.util.StringFormatUtil;
import org.antarcticgardens.cna.energy.EnergyHelper;
import team.reborn.energy.api.EnergyStorage;
import org.antarcticgardens.cna.energy.SimpleEnergyStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

import java.util.List;

public class CarbonBrushesBlockEntity extends KineticBlockEntity {
    private final SimpleEnergyStorage storage;

    private int lastOutput = 0;


    public CarbonBrushesBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        storage = new SimpleEnergyStorage(0)
                .setSupportsInsertion(false);


        setLazyTickRate(20);
    }

    @Override
    protected void write(ValueOutput compound, boolean clientPacket) {
        compound.putInt("lastOutput", lastOutput);
        super.write(compound, clientPacket);
    }

    @Override
    protected void read(ValueInput compound, boolean clientPacket) {
        lastOutput = compound.getIntOr("lastOutput", 0);
        super.read(compound, clientPacket);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour<?>> behaviours) {
        super.addBehaviours(behaviours);
    }

    @Override
    public void invalidate() {
        super.invalidate();
    }


    public SimpleEnergyStorage getEnergyStorage() {
        return storage;
    }

    private int syncOut = 0;

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide()) return;
        Direction facing = getBlockState().getValue(DirectionalKineticBlock.FACING);

        storage.setCapacity(lastOutput * 20L);

        int coilsLeft = CNAConfig.getServer().maxCoils.get();
        lastOutput = 0;
        coilsLeft = processCoil(worldPosition, facing, coilsLeft);
        processCoil(worldPosition, facing.getOpposite(), coilsLeft);

        try (Transaction t = Transaction.openOuter()) {
            EnergyHelper.insertToSurrounding(storage, getBlockPos(), getLevel(), storage.getStoredEnergy(), t);
            t.commit();
        }
    }

    @Override
    public void lazyTick() {
        if (level == null || level.isClientSide()) return;
        if (syncOut > 0) {
            syncOut = 0;
            setChanged();
            sendData();
        }
    }

    private int processCoil(BlockPos pos, Direction dir, int left) {
        if (left <= 0)
            return 0;

        pos = pos.relative(dir);

        if (level.getBlockEntity(pos) instanceof GeneratorCoilBlockEntity coil && coil.getBlockState().getValue(GeneratorCoilBlock.AXIS).test(dir)) {
            int energy = coil.takeGeneratedEnergy();
            lastOutput += energy;
            syncOut += energy;
            storage.internalInsert(energy, false);
            return processCoil(pos, dir, left - 1);
        }
        return left;
    }
    /** Exposes the storage to Team Reborn Energy. Was re-registered from every constructor under ESL. */
    public static void registerEnergyStorage() {
        EnergyStorage.SIDED.registerForBlockEntities((blockEntity, direction) -> ((CarbonBrushesBlockEntity) blockEntity).storage, CNABlockEntityTypes.CARBON_BRUSHES);
    }
    public int getLastOutput() {
        return lastOutput;
    }
}
