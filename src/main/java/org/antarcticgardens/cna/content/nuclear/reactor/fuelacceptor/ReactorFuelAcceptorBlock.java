package org.antarcticgardens.cna.content.nuclear.reactor.fuelacceptor;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.Container;
import com.zurrtum.create.infrastructure.items.ItemInventoryProvider;
import org.antarcticgardens.cna.util.SmartTicker;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.antarcticgardens.cna.CNABlockEntityTypes;
import org.antarcticgardens.cna.content.nuclear.reactor.ReactorBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Exposes the fuel slot the way Create Fly does for its own blocks: through
 * {@link ItemInventoryProvider}, a vanilla {@code WorldlyContainerHolder}, which hoppers, Create's
 * funnels and chutes, and Fabric's item transfer API all read. Under NeoForge this was an item
 * handler capability on the block entity.
 */
public class ReactorFuelAcceptorBlock extends ReactorBlock implements EntityBlock, ItemInventoryProvider<ReactorFuelAcceptorBlockEntity> {
    public ReactorFuelAcceptorBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return CNABlockEntityTypes.REACTOR_FUEL_ACCEPTOR.create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        return SmartTicker.wrap((level1, blockPos, blockState, blockEntity) -> {
            if (!(blockEntity instanceof ReactorFuelAcceptorBlockEntity ent)) return;
            ent.tick(blockPos, level1, blockState);
        });
    }

    @Override
    public Class<ReactorFuelAcceptorBlockEntity> getBlockEntityClass() {
        return ReactorFuelAcceptorBlockEntity.class;
    }

    @Override
    public @Nullable Container getInventory(LevelAccessor world, BlockPos pos, BlockState state,
                                            ReactorFuelAcceptorBlockEntity blockEntity, @Nullable Direction context) {
        return blockEntity.container;
    }

    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getClickedFace());
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

}
