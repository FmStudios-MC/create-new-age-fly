package org.antarcticgardens.cna.content.heat;

import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.antarcticgardens.cna.config.CNAConfig;

import org.jspecify.annotations.Nullable;

public interface HeatBlockEntity {
    float getHeat();
    void addHeat(float amount);

    void setHeat(float amount);

    /**
     * @param from the side of block relative to the block accessing
     */
    default boolean canConnect(Direction from) {
        return true;
    }

    /**
     * @param from the side of block relative to the block accessing
     */
    default boolean canAdd(Direction from) {
        return canConnect(from);
    }
    default float maxHeat() { return 10_000; }

    default float getTierHeat() {
        return getHeat();
    }

    default float @Nullable [] getHeatTiers() {
        return new float[] {};
    }

    default double getHeatTierMultiplier() {
        return 1.0f;
    }

    static <T extends  BlockEntity & HeatBlockEntity> void handleOverheat(T self, Runnable onOverHeat) {
        if (self.getLevel() == null) {
            return;
        }

        double multiplier = CNAConfig.getServer().overheatingMultiplier.get();
        if (multiplier > 0 && self.getHeat() > self.maxHeat() * CNAConfig.getServer().overheatingMultiplier.get()) {
            onOverHeat.run();
        }
    }

    static <T extends  BlockEntity & HeatBlockEntity> void handleOverheat(T self) {
        handleOverheat(self, () -> self.getLevel().setBlock(self.getBlockPos(), Blocks.LAVA.defaultBlockState(), 3));
    }


    static <T extends  BlockEntity & HeatBlockEntity> void transferAround(T self) {
        if (self.getLevel() == null) {
            return;
        }
        float totalToAverage = self.getHeat();
        int totalBlocks = 1;
        HeatBlockEntity[] setters = new HeatBlockEntity[6];
        for (int i = 0 ; i < 6 ; i++) {
            Direction value = Direction.values()[i];
            BlockEntity entity = self.getLevel().getBlockEntity(self.getBlockPos().relative(value));
            if (entity instanceof HeatBlockEntity hbe && hbe.canAdd(value) && self.canAdd(value.getOpposite())) {
                setters[i] = hbe;
                totalToAverage += hbe.getHeat();
                totalBlocks++;
            }
        }
        average(self, totalToAverage, totalBlocks, setters);

        self.setChanged();
    }

    static <T extends BlockEntity & HeatBlockEntity> void trySync(T self) {
        if (self.getLevel() instanceof ServerLevel level && self.getLevel().getGameTime() % 160 == 0) {
            BlockState state = self.getBlockState();
            level.sendBlockUpdated(self.getBlockPos(), state, state, 3);
        }
    }

    static <T extends BlockEntity & HeatBlockEntity> void average(T self, float totalToAverage, int totalBlocks, HeatBlockEntity[] setters) {
        float setAmount = totalToAverage / totalBlocks;
        self.setHeat(setAmount);
        int i = 0;
        for (HeatBlockEntity hbe : setters) {
            if (hbe != null) {
                hbe.setHeat(setAmount);
            }
            i++;
        }
    }

}
