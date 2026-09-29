package org.antarcticgardens.cna.content.electricity.generation.coil;

import com.google.common.collect.Lists;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import com.zurrtum.create.content.kinetics.base.RotatedPillarKineticBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.antarcticgardens.cna.CNATags;
import org.antarcticgardens.cna.CreateNewAge;
import org.antarcticgardens.cna.config.CNAConfig;
import org.antarcticgardens.cna.content.electricity.generation.magnet.IMagneticBlock;
import org.antarcticgardens.cna.util.RelativeBlockPos;

import java.util.List;

public class GeneratorCoilBlockEntity extends KineticBlockEntity {
    public final List<BlockPos> magnetPositions;

    private float lastStress = 0.0f;
    private int generatedEnergy = 0;

    public GeneratorCoilBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);

        RelativeBlockPos relativePosition = new RelativeBlockPos(worldPosition,
                state.getValue(RotatedPillarKineticBlock.AXIS));

        magnetPositions = Lists.newArrayList(
                relativePosition.up(2).right(1).getPos(),
                relativePosition.up(2).getPos(),
                relativePosition.up(2).left(1).getPos(),

                relativePosition.left(2).up(1).getPos(),
                relativePosition.left(2).getPos(),
                relativePosition.left(2).down(1).getPos(),

                relativePosition.down(2).left(1).getPos(),
                relativePosition.down(2).getPos(),
                relativePosition.down(2).right(1).getPos(),

                relativePosition.right(2).down(1).getPos(),
                relativePosition.right(2).getPos(),
                relativePosition.right(2).up(1).getPos()
        );

        setLazyTickRate(20);
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return new AABB(worldPosition).inflate(1);
    }

    private float plainStress;

    @Override
    public float calculateStressApplied() {
        plainStress = super.calculateStressApplied();
        float stress = plainStress;

        for (BlockPos pos : magnetPositions) {
            BlockState state = level.getBlockState(pos);
            if (state.is(CNATags.Block.MAGNET.blockTag)) {
                if (state.getBlock() instanceof IMagneticBlock magneticBlock) {
                    stress += magneticBlock.getStrength();
                } else {
                    for (TagKey<Block> blockTagKey : state.getBlock().builtInRegistryHolder().tags().toList()) {
                        if (blockTagKey.location().getNamespace().equals(CreateNewAge.MOD_ID)) {
                            String path = blockTagKey.location().getPath();
                            if (path.startsWith("magnet/force_")) { 
                                try {
                                    stress += Integer.parseInt(path.substring(13));
                                    break;
                                } catch (NumberFormatException e) {
                                    CreateNewAge.LOGGER.error("BAD TAG " + blockTagKey + " on item " + state.getBlock(), e);
                                }
                            }
                        }
                    }
                }
            }
        }

        this.lastStressApplied = stress;

        return stress;
    }

    @Override
    public void tick() {
        super.tick();
        generatedEnergy = (int) ((lastStressApplied - plainStress) * Math.abs(this.getSpeed()) * CNAConfig.getServer().suToEnergy.get());
    }

    @Override
    public void lazyTick() {
        if (level == null || level.isClientSide())
            return;
        float stress = calculateStressApplied();
        var network = getOrCreateNetwork();

        if (network != null && lastStress != stress) {
            network.updateStressFor(this, stress);
            network.updateStress();
            sendData();

            lastStress = stress;
        }
    }


    public int takeGeneratedEnergy() {
        int energy = generatedEnergy;
        generatedEnergy = 0;
        return energy;
    }
    /** Share of the applied stress that went into generating energy, for the goggle tooltip. */
    public float getEfficiency() {
        return (lastStressApplied - plainStress) / lastStressApplied;
    }
}
