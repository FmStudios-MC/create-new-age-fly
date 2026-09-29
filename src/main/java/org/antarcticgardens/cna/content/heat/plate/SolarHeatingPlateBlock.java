package org.antarcticgardens.cna.content.heat.plate;

import org.antarcticgardens.cna.util.SmartTicker;
import com.zurrtum.create.content.equipment.wrench.IWrenchable;
import java.util.function.Supplier;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.antarcticgardens.cna.CNABlockEntityTypes;
import org.jetbrains.annotations.Nullable;


import static org.antarcticgardens.cna.content.heat.pipe.HeatPipeBlock.massPipe;

public class SolarHeatingPlateBlock extends Block implements EntityBlock, IWrenchable {
    private final Supplier<? extends BlockEntityType<?>> entry;
    private final int strength;

    public SolarHeatingPlateBlock(Properties properties, Supplier<? extends BlockEntityType<?>> entry, int strength) {
        super(properties.strength(4.0f));
        this.entry = entry;
        this.strength = strength;
    }
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return entry.get().create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        final int on = massPipe;
        massPipe++;
        if (massPipe >= 20) {
            massPipe = 0;
        }
        return SmartTicker.wrap((level1, blockPos, blockState, blockEntity) -> {
            if ((level1.getGameTime() + on) % 20 != 0 || !(blockEntity instanceof SolarHeatingPlateBlockEntity ent)) return;
            ent.tick(blockPos, level1, blockState);
        });
    }

    public static SolarHeatingPlateBlock createAdvanced(Properties properties) {
        return  new SolarHeatingPlateBlock(properties, () -> CNABlockEntityTypes.ADVANCED_SOLAR_HEATING_PLATE, 60);
    }

    public static SolarHeatingPlateBlock createBasic(Properties properties) {
        return  new SolarHeatingPlateBlock(properties, () -> CNABlockEntityTypes.BASIC_SOLAR_HEATING_PLATE, 20);
    }

    public int getHeatStrength() {
        return strength;
    }
}
