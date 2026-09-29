package org.antarcticgardens.cna.content.energising;

import com.zurrtum.create.AllShapes;
import com.zurrtum.create.content.kinetics.base.HorizontalKineticBlock;
import com.zurrtum.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.antarcticgardens.cna.CNABlockEntityTypes;
import org.antarcticgardens.cna.config.CNAConfig;
import org.jetbrains.annotations.NotNull;


public class EnergiserBlock extends HorizontalKineticBlock implements IBE<EnergiserBlockEntity> {
    private final int tier;
    
    public EnergiserBlock(Properties properties, int tier) {
        super(properties.strength(2.5F, 1.0F));
        this.tier = tier;
    }

    public static int getStrength(int tier) {
        return (int) ((Math.pow(4, tier)) * CNAConfig.getServer().energiserSpeedMultiplier.get());
    }

    public static long getCapacity(int tier) {
        return (long) (Math.pow(10, tier) * 1000);
    }


    @Override
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        if (context instanceof EntityCollisionContext && ((EntityCollisionContext) context).getEntity() instanceof Player)
            return AllShapes.CASING_14PX.get(Direction.DOWN);

        return AllShapes.MECHANICAL_PROCESSOR_SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction preferredSide = getPreferredHorizontalFacing(context);
        if (preferredSide != null)
            return defaultBlockState().setValue(HORIZONTAL_FACING, preferredSide);
        return super.getStateForPlacement(context);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(HORIZONTAL_FACING).getAxis();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == state.getValue(HORIZONTAL_FACING).getAxis();
    }

    @Override
    public Class<EnergiserBlockEntity> getBlockEntityClass() {
        return EnergiserBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends EnergiserBlockEntity> getBlockEntityType() {
        return CNABlockEntityTypes.ENERGISER;
    }



    public static EnergiserBlock newBasic(Properties properties) {
        return new EnergiserBlock(properties, 1);
    }

    public static EnergiserBlock newAdvanced(Properties properties) {
        return new EnergiserBlock(properties, 2);
    }

    public static EnergiserBlock newReinforced(Properties properties) {
        return new EnergiserBlock(properties, 3);
    }
    public int getTier() {
        return tier;
    }
}
