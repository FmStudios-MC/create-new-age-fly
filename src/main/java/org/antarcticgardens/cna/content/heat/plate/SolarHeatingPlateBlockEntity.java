package org.antarcticgardens.cna.content.heat.plate;

import net.minecraft.world.attribute.EnvironmentAttributes;
import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.world.level.storage.ValueOutput;

import net.minecraft.world.level.storage.ValueInput;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.antarcticgardens.cna.config.CNAConfig;
import org.antarcticgardens.cna.content.heat.HeatBlockEntity;

import java.util.List;

public class SolarHeatingPlateBlockEntity extends SmartBlockEntity implements HeatBlockEntity {
    private final int energyPerSecond;
    private float last;

    public SolarHeatingPlateBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState, int energyPerSecond) {
        super(type, pos, blockState);
        this.energyPerSecond = energyPerSecond;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour<?>> behaviours) {
    }

    public static SolarHeatingPlateBlockEntity createBasic(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        return new SolarHeatingPlateBlockEntity(type, pos, blockState, 20);
    }

    public static SolarHeatingPlateBlockEntity createAdvanced(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        return new SolarHeatingPlateBlockEntity(type, pos, blockState, 60);
    }

    public float heat = 0;

    @Override
    protected void read(ValueInput tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        heat = tag.getFloatOr("heat", 0f);
        last = tag.getFloatOr("last", 0f);
    }

    @Override
    protected void write(ValueOutput tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putFloat("heat", heat);
        tag.putFloat("last", last);
    }

    @Override
    public boolean canConnect(Direction from) {
        return from != Direction.DOWN;
    }

    @Override
    public float getHeat() {
        return heat;
    }

    @Override
    public void addHeat(float amount) {
        heat += amount;
        setChanged();
    }

    @Override
    public void setHeat(float amount) {
        heat = amount;
        setChanged();
    }






    public void tick(BlockPos blockPos, Level world, BlockState blockState) {
        double generationMultiplier = CNAConfig.getServer().solarPanelHeatMultiplier.get();

        int dark = 0;
        if (world.isClientSide()) {
            // update sky could also work but this feels more right.
            double d = 1.0 - (double)(world.getRainLevel(1.0F) * 5.0F) / 16.0;
            double e = 1.0 - (double)(world.getThunderLevel(1.0F) * 5.0F) / 16.0;
            double f = 0.5 + 2.0 * Mth.clamp(Mth.cos(world.environmentAttributes().getValue(EnvironmentAttributes.SUN_ANGLE, blockPos) * Mth.DEG_TO_RAD), -0.25, 0.25);
            dark = (int)((1.0 - f * d * e) * 11.0);
        } else {
            dark = world.getSkyDarken();
        }
        dark *= 2;
        HeatBlockEntity.transferAround(this);
        HeatBlockEntity.handleOverheat(this);

        float light = world.getBrightness(LightLayer.SKY, blockPos.above()) - dark;
        last = (float) Math.max((light/15f)*energyPerSecond*generationMultiplier - Math.max(0, heat - (20 * energyPerSecond*generationMultiplier)), 0);
        addHeat(last);
        HeatBlockEntity.trySync(this);
    }
    public float getLastGenerated() {
        return last;
    }
}
