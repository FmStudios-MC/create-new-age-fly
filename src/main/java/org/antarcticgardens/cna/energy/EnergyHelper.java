package org.antarcticgardens.cna.energy;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;

/** The helpers CNA used from ESL's {@code EnergyHelper} and {@code EnergyStorage}. */
public final class EnergyHelper {
    private EnergyHelper() {
    }

    public static @Nullable EnergyStorage findForBlock(Level level, BlockPos pos, @Nullable Direction side) {
        return EnergyStorage.SIDED.find(level, pos, side);
    }

    public static long moveEnergy(EnergyStorage from, EnergyStorage to, long maxAmount, TransactionContext transaction) {
        return EnergyStorageUtil.move(from, to, maxAmount, transaction);
    }

    /** Pushes up to {@code maxAmount} from {@code from} into the neighbours of {@code pos}, in direction order. */
    public static long insertToSurrounding(EnergyStorage from, BlockPos pos, Level level, long maxAmount, TransactionContext transaction) {
        long moved = 0;
        for (Direction direction : Direction.values()) {
            if (moved >= maxAmount)
                break;
            EnergyStorage target = findForBlock(level, pos.relative(direction), direction.getOpposite());
            if (target != null)
                moved += moveEnergy(from, target, maxAmount - moved, transaction);
        }
        return moved;
    }
}
