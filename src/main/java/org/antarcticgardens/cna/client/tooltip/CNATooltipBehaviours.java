package org.antarcticgardens.cna.client.tooltip;

import com.zurrtum.create.client.api.goggles.IHaveGoggleInformation;
import com.zurrtum.create.client.catnip.lang.LangBuilder;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.tooltip.GeneratingKineticTooltipBehaviour;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.tooltip.KineticTooltipBehaviour;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.tooltip.TooltipBehaviour;
import com.zurrtum.create.client.foundation.utility.CreateLang;
import com.zurrtum.create.content.processing.burner.BlazeBurnerBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.antarcticgardens.cna.config.CNAConfig;
import org.antarcticgardens.cna.content.electricity.connector.ElectricalConnectorBlock;
import org.antarcticgardens.cna.content.electricity.connector.ElectricalConnectorBlockEntity;
import org.antarcticgardens.cna.content.electricity.connector.ElectricalConnectorMode;
import org.antarcticgardens.cna.content.electricity.generation.brushes.CarbonBrushesBlockEntity;
import org.antarcticgardens.cna.content.electricity.generation.coil.GeneratorCoilBlockEntity;
import org.antarcticgardens.cna.content.electricity.light.StreetLightBlockEntity;
import org.antarcticgardens.cna.content.energising.EnergiserBlockEntity;
import org.antarcticgardens.cna.content.heat.heater.HeaterBlock;
import org.antarcticgardens.cna.content.heat.heater.HeaterBlockEntity;
import org.antarcticgardens.cna.content.heat.pipe.HeatPipeBlockEntity;
import org.antarcticgardens.cna.content.heat.plate.SolarHeatingPlateBlockEntity;
import org.antarcticgardens.cna.content.heat.pump.HeatPumpBlockEntity;
import org.antarcticgardens.cna.content.heat.stirling.StirlingEngineBlockEntity;
import org.antarcticgardens.cna.content.motor.MotorBlockEntity;
import org.antarcticgardens.cna.content.nuclear.reactor.rod.ReactorRodBlockEntity;
import org.antarcticgardens.cna.content.nuclear.reactor.vent.ReactorHeatVentBlockEntity;
import org.antarcticgardens.cna.util.StringFormatUtil;

import java.util.List;

/**
 * The goggle tooltips. Create Fly's goggle overlay reads them from a client-side
 * {@link TooltipBehaviour} and never from the block entity, so each {@code addToGoggleTooltip}
 * moved here unchanged from its block entity. Registered in {@link org.antarcticgardens.cna.client.CNABlockEntityBehaviours}.
 */
public final class CNATooltipBehaviours {
    private CNATooltipBehaviours() {
    }

    public static class ElectricalConnector extends TooltipBehaviour<ElectricalConnectorBlockEntity> implements IHaveGoggleInformation {
        public ElectricalConnector(ElectricalConnectorBlockEntity be) {
            super(be);
        }

        @Override
        public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
            CreateLang.translate("tooltip.create_new_age.connector_info")
                    .style(ChatFormatting.WHITE).forGoggles(tooltip);

            CreateLang.translate("tooltip.create_new_age.mode")
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip);

            ElectricalConnectorMode mode = blockEntity.getBlockState().getValue(ElectricalConnectorBlock.MODE);
            CreateLang.translate("tooltip.create_new_age.connector_mode." + mode.getSerializedName())
                    .style(ChatFormatting.AQUA)
                    .forGoggles(tooltip, 1);

            return true;
        }
    }

    public static class StreetLight extends TooltipBehaviour<StreetLightBlockEntity> implements IHaveGoggleInformation {
        public StreetLight(StreetLightBlockEntity be) {
            super(be);
        }

        @Override
        public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
            CreateLang.translate("tooltip.create_new_age.energy_stored")
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip);

            CreateLang.translate("tooltip.create_new_age.energy_storage",
                            StringFormatUtil.formatLong(blockEntity.getEnergyStorage().getAmount()),
                            StringFormatUtil.formatLong(blockEntity.getEnergyStorage().getCapacity()))
                    .style(ChatFormatting.AQUA)
                    .forGoggles(tooltip, 1);

            return true;
        }
    }

    /** Upstream showed only the energy output here, without the kinetic stress lines. */
    public static class CarbonBrushes extends KineticTooltipBehaviour<CarbonBrushesBlockEntity> {
        public CarbonBrushes(CarbonBrushesBlockEntity be) {
            super(be);
        }

        @Override
        public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
            CreateLang.translate("tooltip.create_new_age.energy_stats")
                    .style(ChatFormatting.WHITE).forGoggles(tooltip);

            CreateLang.translate("tooltip.create_new_age.energy_output")
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip);

            CreateLang.translate("tooltip.create_new_age.energy_per_tick", StringFormatUtil.formatLong(blockEntity.getLastOutput()))
                    .style(ChatFormatting.AQUA)
                    .forGoggles(tooltip, 1);

            return true;
        }
    }

    public static class GeneratorCoil extends KineticTooltipBehaviour<GeneratorCoilBlockEntity> {
        public GeneratorCoil(GeneratorCoilBlockEntity be) {
            super(be);
        }

        @Override
        public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
            CreateLang.translate("tooltip.create_new_age.efficiency").style(ChatFormatting.GRAY).forGoggles(tooltip, 1);
            CreateLang.translate("tooltip.create_new_age.percent", StringFormatUtil.formatPercentFloat(blockEntity.getEfficiency()))
                    .style(ChatFormatting.AQUA).forGoggles(tooltip, 2);
            return super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        }
    }

    public static class Energiser extends KineticTooltipBehaviour<EnergiserBlockEntity> {
        public Energiser(EnergiserBlockEntity be) {
            super(be);
        }

        @Override
        public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
            CreateLang.translate("tooltip.create_new_age.energy_stats")
                    .style(ChatFormatting.WHITE).forGoggles(tooltip);

            CreateLang.translate("tooltip.create_new_age.energy_stored")
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip);
            CreateLang.translate("tooltip.create_new_age.energy_storage",
                            StringFormatUtil.formatLong(blockEntity.getEnergyStorage().getAmount()),
                            StringFormatUtil.formatLong(blockEntity.getEnergyStorage().getCapacity()))
                    .style(ChatFormatting.AQUA).forGoggles(tooltip, 1);

            if (blockEntity.lastCharged != -1) {
                CreateLang.translate("tooltip.create_new_age.energy_usage")
                        .style(ChatFormatting.GRAY)
                        .forGoggles(tooltip);
                CreateLang.translate("tooltip.create_new_age.energy_per_tick", StringFormatUtil.formatLong(blockEntity.lastCharged))
                        .style(ChatFormatting.AQUA).forGoggles(tooltip, 1);
            }

            return super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        }
    }

    public static class Motor extends GeneratingKineticTooltipBehaviour<MotorBlockEntity> {
        public Motor(MotorBlockEntity be) {
            super(be);
        }

        @Override
        public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
            CreateLang.translate("tooltip.create_new_age.energy_stored")
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip);

            CreateLang.translate("tooltip.create_new_age.energy_storage",
                            StringFormatUtil.formatLong(blockEntity.getEnergyStorage().getAmount()),
                            StringFormatUtil.formatLong(blockEntity.getEnergyStorage().getCapacity()))
                    .style(ChatFormatting.AQUA)
                    .forGoggles(tooltip, 1);

            CreateLang.translate("tooltip.create_new_age.using")
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip);

            CreateLang.translate("tooltip.create_new_age.energy_per_tick", StringFormatUtil.formatLong(blockEntity.getLastConsumed()))
                    .style(ChatFormatting.AQUA)
                    .forGoggles(tooltip, 1);

            super.addToGoggleTooltip(tooltip, isPlayerSneaking);

            return true;
        }
    }

    public static class StirlingEngine extends GeneratingKineticTooltipBehaviour<StirlingEngineBlockEntity> {
        public StirlingEngine(StirlingEngineBlockEntity be) {
            super(be);
        }

        @Override
        public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
            HeatTooltips.addHeat(blockEntity, tooltip);

            CreateLang.translate("tooltip.create_new_age.using")
                    .style(ChatFormatting.GRAY).forGoggles(tooltip, 1);
            // getTierHeat() is speed * 3.125, i.e. 50/16
            CreateLang.translate("tooltip.create_new_age.temperature.ps", StringFormatUtil.formatFloat(blockEntity.getTierHeat()))
                    .style(ChatFormatting.AQUA).forGoggles(tooltip, 2);

            return super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        }
    }

    public static class Heater extends TooltipBehaviour<HeaterBlockEntity> implements IHaveGoggleInformation {
        public Heater(HeaterBlockEntity be) {
            super(be);
        }

        @Override
        public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
            HeatTooltips.addHeat(blockEntity, tooltip);
            BlazeBurnerBlock.HeatLevel strength = blockEntity.getBlockState().getValue(HeaterBlock.STRENGTH);
            double mult = CNAConfig.getServer().heaterRequiredHeatMultiplier.get();

            double heat = switch (strength) {
                case SMOULDERING -> 50 * mult;
                case FADING -> 100 * mult;
                case KINDLED -> 400 * mult;
                case SEETHING -> 500 * mult;
                default -> 0;
            };

            CreateLang.translate("tooltip.create_new_age.releasing")
                    .style(ChatFormatting.GRAY).forGoggles(tooltip, 1);
            CreateLang.translate("tooltip.create_new_age.temperature.ps", StringFormatUtil.formatFloat((float) heat))
                    .style(ChatFormatting.AQUA).forGoggles(tooltip, 2);
            return true;
        }
    }

    public static class HeatPipe extends TooltipBehaviour<HeatPipeBlockEntity> implements IHaveGoggleInformation {
        public HeatPipe(HeatPipeBlockEntity be) {
            super(be);
        }

        @Override
        public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
            HeatTooltips.addHeat(blockEntity, tooltip);

            if (blockEntity.generating > 0.05) {
                CreateLang.translate("tooltip.create_new_age.generating")
                        .style(ChatFormatting.GRAY).forGoggles(tooltip, 1);
                CreateLang.translate("tooltip.create_new_age.temperature.ps", StringFormatUtil.formatFloat(blockEntity.generating))
                        .style(ChatFormatting.AQUA).forGoggles(tooltip, 2);
            }

            return true;
        }
    }

    public static class SolarHeatingPlate extends TooltipBehaviour<SolarHeatingPlateBlockEntity> implements IHaveGoggleInformation {
        public SolarHeatingPlate(SolarHeatingPlateBlockEntity be) {
            super(be);
        }

        @Override
        public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
            HeatTooltips.addHeat(blockEntity, tooltip);

            CreateLang.translate("tooltip.create_new_age.generating")
                    .style(ChatFormatting.GRAY).forGoggles(tooltip, 1);
            CreateLang.translate("tooltip.create_new_age.temperature.ps", StringFormatUtil.formatFloat(blockEntity.getLastGenerated()))
                    .style(ChatFormatting.AQUA).forGoggles(tooltip, 2);

            return true;
        }
    }

    public static class HeatPump extends TooltipBehaviour<HeatPumpBlockEntity> implements IHaveGoggleInformation {
        public HeatPump(HeatPumpBlockEntity be) {
            super(be);
        }

        @Override
        public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
            CreateLang.translate("tooltip.create_new_age.pump").style(ChatFormatting.GRAY).forGoggles(tooltip, 1);
            CreateLang.translate("tooltip.create_new_age.temperature.ps", StringFormatUtil.formatFloat(blockEntity.lastPump))
                    .style(ChatFormatting.AQUA).forGoggles(tooltip, 2);
            return true;
        }
    }

    public static class ReactorRod extends TooltipBehaviour<ReactorRodBlockEntity> implements IHaveGoggleInformation {
        public ReactorRod(ReactorRodBlockEntity be) {
            super(be);
        }

        @Override
        public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
            HeatTooltips.addHeat(blockEntity, tooltip);

            CreateLang.translate("tooltip.create_new_age.generating")
                    .style(ChatFormatting.GRAY).forGoggles(tooltip, 1);
            CreateLang.translate("tooltip.create_new_age.temperature.ps", StringFormatUtil.formatFloat((float) (blockEntity.last * 20)))
                    .style(ChatFormatting.AQUA).forGoggles(tooltip, 2);

            CreateLang.translate("tooltip.create_new_age.fuel_ticks_left")
                    .style(ChatFormatting.GRAY).forGoggles(tooltip, 1);

            LangBuilder builder = CreateLang.text(StringFormatUtil.formatFloat(blockEntity.fuel));
            builder.style(ChatFormatting.AQUA).forGoggles(tooltip, 2);

            return true;
        }
    }

    public static class ReactorHeatVent extends TooltipBehaviour<ReactorHeatVentBlockEntity> implements IHaveGoggleInformation {
        public ReactorHeatVent(ReactorHeatVentBlockEntity be) {
            super(be);
        }

        @Override
        public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
            HeatTooltips.addHeat(blockEntity, tooltip);

            CreateLang.translate("tooltip.create_new_age.extracting")
                    .style(ChatFormatting.GRAY).forGoggles(tooltip, 1);
            CreateLang.translate("tooltip.create_new_age.temperature", StringFormatUtil.formatFloat(blockEntity.getLastExtracted()))
                    .style(ChatFormatting.AQUA).forGoggles(tooltip, 2);
            return true;
        }
    }
}
