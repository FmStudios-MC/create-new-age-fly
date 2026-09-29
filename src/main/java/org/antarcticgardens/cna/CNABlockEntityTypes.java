package org.antarcticgardens.cna;

import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer;
import com.zurrtum.create.client.content.kinetics.base.OrientedRotatingVisual;
import com.zurrtum.create.client.content.kinetics.base.SingleAxisRotatingVisual;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import org.antarcticgardens.cna.content.electricity.connector.ElectricalConnectorBlockEntity;
import org.antarcticgardens.cna.content.electricity.connector.ElectricalConnectorRenderer;
import org.antarcticgardens.cna.content.electricity.generation.brushes.CarbonBrushesBlockEntity;
import org.antarcticgardens.cna.content.electricity.generation.brushes.CarbonBrushesRenderer;
import org.antarcticgardens.cna.content.electricity.generation.coil.GeneratorCoilBlockEntity;
import org.antarcticgardens.cna.content.electricity.light.StreetLightBlockEntity;
import org.antarcticgardens.cna.content.energising.EnergiserBlockEntity;
import org.antarcticgardens.cna.content.energising.EnergiserRenderer;
import org.antarcticgardens.cna.content.heat.heater.HeaterBlockEntity;
import org.antarcticgardens.cna.content.heat.pipe.EncasedHeatPipeBlockEntity;
import org.antarcticgardens.cna.content.heat.pipe.HeatPipeBlockEntity;
import org.antarcticgardens.cna.content.heat.plate.SolarHeatingPlateBlockEntity;
import org.antarcticgardens.cna.content.heat.pump.HeatPumpBlockEntity;
import org.antarcticgardens.cna.content.heat.stirling.StirlingEngineBlockEntity;
import org.antarcticgardens.cna.content.heat.stirling.StirlingEngineVisual;
import org.antarcticgardens.cna.content.heat.stirling.StirlingEngineRenderer;
import org.antarcticgardens.cna.content.motor.MotorBlockEntity;
import org.antarcticgardens.cna.content.motor.extension.MotorExtensionBlockEntity;
import org.antarcticgardens.cna.content.motor.extension.variants.AdvancedMotorExtensionVariant;
import org.antarcticgardens.cna.content.motor.extension.variants.BasicMotorExtensionVariant;
import org.antarcticgardens.cna.content.motor.variants.AdvancedMotorVariant;
import org.antarcticgardens.cna.content.motor.variants.BasicMotorVariant;
import org.antarcticgardens.cna.content.motor.variants.ReinforcedMotorVariant;
import org.antarcticgardens.cna.content.nuclear.reactor.fuelacceptor.ReactorFuelAcceptorBlockEntity;
import org.antarcticgardens.cna.content.nuclear.reactor.rod.ReactorRodBlockEntity;
import org.antarcticgardens.cna.content.nuclear.reactor.vent.ReactorHeatVentBlockEntity;
import org.antarcticgardens.cna.rendering.HalfShaftRenderer;

import static org.antarcticgardens.cna.CreateNewAge.REGISTRATE;

public class CNABlockEntityTypes {
    public static final BlockEntityEntry<EnergiserBlockEntity> ENERGISER = REGISTRATE
            .blockEntity("energiser", EnergiserBlockEntity::new)
            .visual(() -> SingleAxisRotatingVisual::shaft)
            .validBlocks(CNABlocks.BASIC_ENERGISER, CNABlocks.ADVANCED_ENERGISER, CNABlocks.REINFORCED_ENERGISER)
            .renderer(() -> EnergiserRenderer::new)
            .register();

    public static final BlockEntityEntry<ElectricalConnectorBlockEntity> ELECTRICAL_CONNECTOR = REGISTRATE
            .blockEntity("electrical_connector", ElectricalConnectorBlockEntity::new)
            .validBlocks(CNABlocks.ELECTRICAL_CONNECTOR)
            .renderer(() -> ElectricalConnectorRenderer::new)
            .register();

    public static final BlockEntityEntry<GeneratorCoilBlockEntity> GENERATOR_COIL = REGISTRATE
            .blockEntity("generator_coil", GeneratorCoilBlockEntity::new)
            .visual(() ->  SingleAxisRotatingVisual.of(CNAPartialModels.GENERATOR_COIL), false)
            .validBlocks(CNABlocks.GENERATOR_COIL)
            .renderer(() -> KineticBlockEntityRenderer::new)
            .register();

    public static final BlockEntityEntry<CarbonBrushesBlockEntity> CARBON_BRUSHES = REGISTRATE
            .blockEntity("carbon_brushes", CarbonBrushesBlockEntity::new)
            .visual(() -> SingleAxisRotatingVisual::shaft)
            .validBlocks(CNABlocks.CARBON_BRUSHES)
            .renderer(() -> CarbonBrushesRenderer::new)
            .register();

    public static final BlockEntityEntry<HeatPipeBlockEntity> HEAT_PIPE = REGISTRATE
            .blockEntity("heat_pipe", HeatPipeBlockEntity::new)
            .validBlocks(CNABlocks.HEAT_PIPE)
            .register();

    public static final BlockEntityEntry<EncasedHeatPipeBlockEntity> ENCASED_HEAT_PIPE = REGISTRATE
            .blockEntity("encased_heat_pipe", EncasedHeatPipeBlockEntity::new)
            .validBlocks(CNABlocks.ENCASED_HEAT_PIPE, CNABlocks.REACTOR_ENCASED_HEAT_PIPE)
            .register();

    public static final BlockEntityEntry<HeatPumpBlockEntity> HEAT_PUMP = REGISTRATE
            .blockEntity("heat_pump", HeatPumpBlockEntity::new)
            .validBlocks(CNABlocks.HEAT_PUMP)
            .register();

    public static final BlockEntityEntry<HeaterBlockEntity> HEATER = REGISTRATE
            .blockEntity("heater", HeaterBlockEntity::new)
            .validBlocks(CNABlocks.HEATER)
            .register();


    public static final BlockEntityEntry<SolarHeatingPlateBlockEntity> BASIC_SOLAR_HEATING_PLATE = REGISTRATE
            .blockEntity("basic_solar_heating_plate", SolarHeatingPlateBlockEntity::createBasic)
            .validBlocks(CNABlocks.BASIC_SOLAR_HEATING_PLATE)
            .register();

    public static final BlockEntityEntry<SolarHeatingPlateBlockEntity> ADVANCED_SOLAR_HEATING_PLATE = REGISTRATE
            .blockEntity("advanced_solar_heating_plate", SolarHeatingPlateBlockEntity::createAdvanced)
            .validBlocks(CNABlocks.ADVANCED_SOLAR_HEATING_PLATE)
            .register();


    public static final BlockEntityEntry<ReactorRodBlockEntity> REACTOR_ROD = REGISTRATE
            .blockEntity("reactor_rod", ReactorRodBlockEntity::new)
            .validBlocks(CNABlocks.REACTOR_ROD)
            .register();

    public static final BlockEntityEntry<ReactorFuelAcceptorBlockEntity> REACTOR_FUEL_ACCEPTOR = REGISTRATE
            .blockEntity("reactor_fuel_acceptor", CreateNewAge.getInstance().getPlatform()::platformReactorFuelAcceptorBlockEntity)
            .validBlocks(CNABlocks.REACTOR_FUEL_ACCEPTOR)
            .register();

    public static final BlockEntityEntry<ReactorHeatVentBlockEntity> REACTOR_HEAT_VENT = REGISTRATE
            .blockEntity("reactor_heat_vent", ReactorHeatVentBlockEntity::new)
            .validBlocks(CNABlocks.REACTOR_HEAT_VENT)
            .register();


    public static final BlockEntityEntry<StirlingEngineBlockEntity> STIRLING_ENGINE = REGISTRATE
            .blockEntity("stirling_engine", StirlingEngineBlockEntity::new)
            .visual(() -> StirlingEngineVisual::new)
            .validBlocks(CNABlocks.STIRLING_ENGINE)
            .renderer(() -> StirlingEngineRenderer::new)
            .register();


    public static final BlockEntityEntry<MotorBlockEntity> BASIC_MOTOR = REGISTRATE
            .blockEntity("basic_motor", MotorBlockEntity.create(new BasicMotorVariant()))
            .visual(() -> OrientedRotatingVisual.of(AllPartialModels.SHAFT_HALF))
            .validBlocks(CNABlocks.BASIC_MOTOR)
            .renderer(() -> HalfShaftRenderer::new)
            .register();

    public static final BlockEntityEntry<MotorBlockEntity> ADVANCED_MOTOR = REGISTRATE
            .blockEntity("advanced_motor", MotorBlockEntity.create(new AdvancedMotorVariant()))
            .visual(() -> OrientedRotatingVisual.of(AllPartialModels.SHAFT_HALF))
            .validBlocks(CNABlocks.ADVANCED_MOTOR)
            .renderer(() -> HalfShaftRenderer::new)
            .register();

    public static final BlockEntityEntry<MotorBlockEntity> REINFORCED_MOTOR = REGISTRATE
            .blockEntity("reinforced_motor", MotorBlockEntity.create(new ReinforcedMotorVariant()))
            .visual(() -> OrientedRotatingVisual.of(AllPartialModels.SHAFT_HALF))
            .validBlocks(CNABlocks.REINFORCED_MOTOR)
            .renderer(() -> HalfShaftRenderer::new)
            .register();


    public static final BlockEntityEntry<MotorExtensionBlockEntity> BASIC_MOTOR_EXTENSION = REGISTRATE
            .blockEntity("basic_motor_extension", MotorExtensionBlockEntity.create(new BasicMotorExtensionVariant()))
            .validBlocks(CNABlocks.BASIC_MOTOR_EXTENSION)
            .register();

    public static final BlockEntityEntry<MotorExtensionBlockEntity> ADVANCED_MOTOR_EXTENSION = REGISTRATE
            .blockEntity("advanced_motor_extension", MotorExtensionBlockEntity.create(new AdvancedMotorExtensionVariant()))
            .validBlocks(CNABlocks.ADVANCED_MOTOR_EXTENSION)
            .register();

    public static final BlockEntityEntry<StreetLightBlockEntity> STREET_LIGHT = REGISTRATE
            .blockEntity("street_light", StreetLightBlockEntity::new)
            .validBlocks(CNABlocks.STREET_LIGHT)
            .renderer(() -> ElectricalConnectorRenderer::new)
            .register();

    public static void load() {  }
}
