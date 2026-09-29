package org.antarcticgardens.cna;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.antarcticgardens.cna.content.electricity.connector.ElectricalConnectorBlockEntity;
import org.antarcticgardens.cna.content.electricity.generation.brushes.CarbonBrushesBlockEntity;
import org.antarcticgardens.cna.content.electricity.generation.coil.GeneratorCoilBlockEntity;
import org.antarcticgardens.cna.content.electricity.light.StreetLightBlockEntity;
import org.antarcticgardens.cna.content.energising.EnergiserBlockEntity;
import org.antarcticgardens.cna.content.heat.heater.HeaterBlockEntity;
import org.antarcticgardens.cna.content.heat.pipe.EncasedHeatPipeBlockEntity;
import org.antarcticgardens.cna.content.heat.pipe.HeatPipeBlockEntity;
import org.antarcticgardens.cna.content.heat.plate.SolarHeatingPlateBlockEntity;
import org.antarcticgardens.cna.content.heat.pump.HeatPumpBlockEntity;
import org.antarcticgardens.cna.content.heat.stirling.StirlingEngineBlockEntity;
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

import java.util.Set;

/**
 * Renderers and Flywheel visuals, which Registrate chained onto these, are registered from the
 * client entrypoint.
 */
public class CNABlockEntityTypes {
    public static final BlockEntityType<EnergiserBlockEntity> ENERGISER = register("energiser", EnergiserBlockEntity::new,
            CNABlocks.BASIC_ENERGISER, CNABlocks.ADVANCED_ENERGISER, CNABlocks.REINFORCED_ENERGISER);

    public static final BlockEntityType<ElectricalConnectorBlockEntity> ELECTRICAL_CONNECTOR = register("electrical_connector",
            ElectricalConnectorBlockEntity::new, CNABlocks.ELECTRICAL_CONNECTOR);

    public static final BlockEntityType<GeneratorCoilBlockEntity> GENERATOR_COIL = register("generator_coil",
            GeneratorCoilBlockEntity::new, CNABlocks.GENERATOR_COIL);

    public static final BlockEntityType<CarbonBrushesBlockEntity> CARBON_BRUSHES = register("carbon_brushes",
            CarbonBrushesBlockEntity::new, CNABlocks.CARBON_BRUSHES);

    public static final BlockEntityType<HeatPipeBlockEntity> HEAT_PIPE = register("heat_pipe",
            HeatPipeBlockEntity::new, CNABlocks.HEAT_PIPE);

    public static final BlockEntityType<EncasedHeatPipeBlockEntity> ENCASED_HEAT_PIPE = register("encased_heat_pipe",
            EncasedHeatPipeBlockEntity::new, CNABlocks.ENCASED_HEAT_PIPE, CNABlocks.REACTOR_ENCASED_HEAT_PIPE);

    public static final BlockEntityType<HeatPumpBlockEntity> HEAT_PUMP = register("heat_pump",
            HeatPumpBlockEntity::new, CNABlocks.HEAT_PUMP);

    public static final BlockEntityType<HeaterBlockEntity> HEATER = register("heater",
            HeaterBlockEntity::new, CNABlocks.HEATER);

    public static final BlockEntityType<SolarHeatingPlateBlockEntity> BASIC_SOLAR_HEATING_PLATE = register("basic_solar_heating_plate",
            SolarHeatingPlateBlockEntity::createBasic, CNABlocks.BASIC_SOLAR_HEATING_PLATE);

    public static final BlockEntityType<SolarHeatingPlateBlockEntity> ADVANCED_SOLAR_HEATING_PLATE = register("advanced_solar_heating_plate",
            SolarHeatingPlateBlockEntity::createAdvanced, CNABlocks.ADVANCED_SOLAR_HEATING_PLATE);

    public static final BlockEntityType<ReactorRodBlockEntity> REACTOR_ROD = register("reactor_rod",
            ReactorRodBlockEntity::new, CNABlocks.REACTOR_ROD);

    public static final BlockEntityType<ReactorFuelAcceptorBlockEntity> REACTOR_FUEL_ACCEPTOR = register("reactor_fuel_acceptor",
            ReactorFuelAcceptorBlockEntity::new, CNABlocks.REACTOR_FUEL_ACCEPTOR);

    public static final BlockEntityType<ReactorHeatVentBlockEntity> REACTOR_HEAT_VENT = register("reactor_heat_vent",
            ReactorHeatVentBlockEntity::new, CNABlocks.REACTOR_HEAT_VENT);

    public static final BlockEntityType<StirlingEngineBlockEntity> STIRLING_ENGINE = register("stirling_engine",
            StirlingEngineBlockEntity::new, CNABlocks.STIRLING_ENGINE);

    public static final BlockEntityType<MotorBlockEntity> BASIC_MOTOR = register("basic_motor",
            MotorBlockEntity.create(new BasicMotorVariant()), CNABlocks.BASIC_MOTOR);

    public static final BlockEntityType<MotorBlockEntity> ADVANCED_MOTOR = register("advanced_motor",
            MotorBlockEntity.create(new AdvancedMotorVariant()), CNABlocks.ADVANCED_MOTOR);

    public static final BlockEntityType<MotorBlockEntity> REINFORCED_MOTOR = register("reinforced_motor",
            MotorBlockEntity.create(new ReinforcedMotorVariant()), CNABlocks.REINFORCED_MOTOR);

    public static final BlockEntityType<MotorExtensionBlockEntity> BASIC_MOTOR_EXTENSION = register("basic_motor_extension",
            MotorExtensionBlockEntity.create(new BasicMotorExtensionVariant()), CNABlocks.BASIC_MOTOR_EXTENSION);

    public static final BlockEntityType<MotorExtensionBlockEntity> ADVANCED_MOTOR_EXTENSION = register("advanced_motor_extension",
            MotorExtensionBlockEntity.create(new AdvancedMotorExtensionVariant()), CNABlocks.ADVANCED_MOTOR_EXTENSION);

    public static final BlockEntityType<StreetLightBlockEntity> STREET_LIGHT = register("street_light",
            StreetLightBlockEntity::new, CNABlocks.STREET_LIGHT);

    static void init() {
    }

    /**
     * CNA's block entities take their type as a constructor argument, as they did under Registrate.
     * 26.2's {@link BlockEntityType.BlockEntitySupplier} only passes the position and state, so the
     * type is handed over once it exists: the supplier runs when a block entity is created, long
     * after registration has returned.
     */
    @FunctionalInterface
    public interface Factory<T extends BlockEntity> {
        T create(BlockEntityType<T> type, BlockPos pos, BlockState state);
    }

    @SuppressWarnings("unchecked")
    private static <T extends BlockEntity> BlockEntityType<T> register(String name, Factory<? extends T> factory, Block... blocks) {
        BlockEntityType<?>[] self = new BlockEntityType<?>[1];
        BlockEntityType<T> type = new BlockEntityType<>(
                (pos, state) -> ((Factory<T>) factory).create((BlockEntityType<T>) self[0], pos, state),
                Set.of(blocks));
        self[0] = type;
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(CreateNewAge.MOD_ID, name), type);
    }
}
