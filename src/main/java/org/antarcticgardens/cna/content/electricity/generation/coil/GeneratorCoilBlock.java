package org.antarcticgardens.cna.content.electricity.generation.coil;

import com.zurrtum.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.zurrtum.create.foundation.block.IBE;
import com.zurrtum.create.catnip.placement.IPlacementHelper;
import com.zurrtum.create.catnip.placement.PlacementHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.antarcticgardens.cna.CNABlockEntityTypes;
import org.antarcticgardens.cna.CreateNewAge;

public class GeneratorCoilBlock extends RotatedPillarKineticBlock implements IBE<GeneratorCoilBlockEntity> {
    public GeneratorCoilBlock(Properties properties) {
        super(properties.strength(2.0F, 1.0F));
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(AXIS);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == state.getValue(AXIS);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.isShiftKeyDown())
            return InteractionResult.TRY_WITH_EMPTY_HAND;

        ItemStack itemInHand = player.getItemInHand(hand);

        IPlacementHelper helper = PlacementHelpers.get(CreateNewAge.getMagnetPlacementHelperId());
        if (helper.matchesItem(itemInHand))
            return helper.getOffset(player, level, state, pos, hitResult)
                    .placeInWorld(level, (BlockItem) itemInHand.getItem(), player, hand);

        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    public Class<GeneratorCoilBlockEntity> getBlockEntityClass() {
        return GeneratorCoilBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends GeneratorCoilBlockEntity> getBlockEntityType() {
        return CNABlockEntityTypes.GENERATOR_COIL;
    }
}
