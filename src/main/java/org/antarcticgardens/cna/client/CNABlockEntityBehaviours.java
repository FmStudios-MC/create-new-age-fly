package org.antarcticgardens.cna.client;

import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.audio.KineticAudioBehaviour;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.antarcticgardens.cna.CNABlockEntityTypes;
import org.antarcticgardens.cna.client.behaviour.MotorExtensionScrollBehaviour;
import org.antarcticgardens.cna.client.behaviour.MotorScrollBehaviour;
import org.antarcticgardens.cna.client.behaviour.StreetLightScrollBehaviour;
import org.antarcticgardens.cna.client.tooltip.CNATooltipBehaviours;

import java.util.function.Function;

/**
 * Client halves of block entity behaviours, keyed by block entity type, mirroring Create Fly's
 * {@code AllBlockEntityBehaviours}. Create Fly attaches these on the first client tick; nothing on
 * the block entity itself is consulted for goggle tooltips or value boxes.
 */
public class CNABlockEntityBehaviours {
    @SuppressWarnings("unchecked")
    @SafeVarargs
    private static <T extends SmartBlockEntity> void add(BlockEntityType<T> type, Function<T, BlockEntityBehaviour<?>>... factories) {
        for (Function<T, BlockEntityBehaviour<?>> factory : factories)
            BlockEntityBehaviour.CLIENT_REGISTRY.add(type, (Function<SmartBlockEntity, BlockEntityBehaviour<?>>) factory);
    }

    public static void register() {
        add(CNABlockEntityTypes.BASIC_MOTOR, CNATooltipBehaviours.Motor::new, KineticAudioBehaviour::new, MotorScrollBehaviour::new);
        add(CNABlockEntityTypes.ADVANCED_MOTOR, CNATooltipBehaviours.Motor::new, KineticAudioBehaviour::new, MotorScrollBehaviour::new);
        add(CNABlockEntityTypes.REINFORCED_MOTOR, CNATooltipBehaviours.Motor::new, KineticAudioBehaviour::new, MotorScrollBehaviour::new);
        add(CNABlockEntityTypes.BASIC_MOTOR_EXTENSION, MotorExtensionScrollBehaviour::new);
        add(CNABlockEntityTypes.ADVANCED_MOTOR_EXTENSION, MotorExtensionScrollBehaviour::new);

        add(CNABlockEntityTypes.ENERGISER, CNATooltipBehaviours.Energiser::new, KineticAudioBehaviour::new);
        add(CNABlockEntityTypes.GENERATOR_COIL, CNATooltipBehaviours.GeneratorCoil::new, KineticAudioBehaviour::new);
        add(CNABlockEntityTypes.CARBON_BRUSHES, CNATooltipBehaviours.CarbonBrushes::new, KineticAudioBehaviour::new);
        add(CNABlockEntityTypes.STIRLING_ENGINE, CNATooltipBehaviours.StirlingEngine::new, KineticAudioBehaviour::new);

        add(CNABlockEntityTypes.ELECTRICAL_CONNECTOR, CNATooltipBehaviours.ElectricalConnector::new);
        add(CNABlockEntityTypes.STREET_LIGHT, CNATooltipBehaviours.StreetLight::new, StreetLightScrollBehaviour::new);

        add(CNABlockEntityTypes.HEATER, CNATooltipBehaviours.Heater::new);
        add(CNABlockEntityTypes.HEAT_PIPE, CNATooltipBehaviours.HeatPipe::new);
        add(CNABlockEntityTypes.ENCASED_HEAT_PIPE, CNATooltipBehaviours.HeatPipe::new);
        add(CNABlockEntityTypes.HEAT_PUMP, CNATooltipBehaviours.HeatPump::new);
        add(CNABlockEntityTypes.BASIC_SOLAR_HEATING_PLATE, CNATooltipBehaviours.SolarHeatingPlate::new);
        add(CNABlockEntityTypes.ADVANCED_SOLAR_HEATING_PLATE, CNATooltipBehaviours.SolarHeatingPlate::new);
        add(CNABlockEntityTypes.REACTOR_ROD, CNATooltipBehaviours.ReactorRod::new);
        add(CNABlockEntityTypes.REACTOR_HEAT_VENT, CNATooltipBehaviours.ReactorHeatVent::new);
    }
}
