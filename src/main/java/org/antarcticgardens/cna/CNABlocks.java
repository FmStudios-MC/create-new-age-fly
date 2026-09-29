package org.antarcticgardens.cna;

import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.api.stress.BlockStressValues;
import com.zurrtum.create.content.decoration.encasing.CasingBlock;
import com.zurrtum.create.content.decoration.encasing.EncasingRegistry;
import com.zurrtum.create.content.processing.AssemblyOperatorBlockItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import org.antarcticgardens.cna.content.electricity.connector.ElectricalConnectorBlock;
import org.antarcticgardens.cna.content.electricity.generation.brushes.CarbonBrushesBlock;
import org.antarcticgardens.cna.content.electricity.generation.brushes.CarbonBrushesItem;
import org.antarcticgardens.cna.content.electricity.generation.coil.GeneratorCoilBlock;
import org.antarcticgardens.cna.content.electricity.generation.magnet.ImplementedMagnetBlock;
import org.antarcticgardens.cna.content.electricity.light.LampPostBlock;
import org.antarcticgardens.cna.content.electricity.light.StreetLightBlock;
import org.antarcticgardens.cna.content.energising.EnergiserBlock;
import org.antarcticgardens.cna.content.energising.EnergisingBlockItem;
import org.antarcticgardens.cna.content.heat.heater.HeaterBlock;
import org.antarcticgardens.cna.content.heat.pipe.EncasedHeatPipeBlock;
import org.antarcticgardens.cna.content.heat.pipe.HeatPipeBlock;
import org.antarcticgardens.cna.content.heat.pipe.ReactorEncasedHeatPipeBlock;
import org.antarcticgardens.cna.content.heat.plate.SolarHeatingPlateBlock;
import org.antarcticgardens.cna.content.heat.pump.HeatPumpBlock;
import org.antarcticgardens.cna.content.heat.stirling.StirlingEngineBlock;
import org.antarcticgardens.cna.content.heat.stirling.StirlingEngineItem;
import org.antarcticgardens.cna.content.motor.MotorBlock;
import org.antarcticgardens.cna.content.motor.MotorBlockItem;
import org.antarcticgardens.cna.content.motor.extension.MotorExtensionBlock;
import org.antarcticgardens.cna.content.motor.extension.variants.AdvancedMotorExtensionVariant;
import org.antarcticgardens.cna.content.motor.extension.variants.BasicMotorExtensionVariant;
import org.antarcticgardens.cna.content.motor.variants.AdvancedMotorVariant;
import org.antarcticgardens.cna.content.motor.variants.BasicMotorVariant;
import org.antarcticgardens.cna.content.motor.variants.ReinforcedMotorVariant;
import org.antarcticgardens.cna.content.nuclear.CoriumBlock;
import org.antarcticgardens.cna.content.nuclear.SolidCoriumBlock;
import org.antarcticgardens.cna.content.nuclear.reactor.ReactorCasingBlock;
import org.antarcticgardens.cna.content.nuclear.reactor.ReactorTransparentBlock;
import org.antarcticgardens.cna.content.nuclear.reactor.fuelacceptor.ReactorFuelAcceptorBlock;
import org.antarcticgardens.cna.content.nuclear.reactor.rod.ReactorRodBlock;
import org.antarcticgardens.cna.content.nuclear.reactor.vent.ReactorHeatVentBlock;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Registered eagerly in Create Fly's vanilla style; Registrate does not exist on 26.2.
 * <p>
 * Blockstates, block models, loot tables and tags that Registrate used to generate from the builder
 * chains live as JSON under {@code src/generated/resources}. Connected textures and casing
 * connectivity are client-side in Create Fly and are registered from {@code CNAClientBlocks}.
 * Render layers are no longer declared: 26.2 derives the chunk layer from the textures.
 */
public class CNABlocks {
    public static final EnergiserBlock BASIC_ENERGISER = register("basic_energiser", EnergiserBlock::newBasic,
            Properties.of().requiresCorrectToolForDrops().noOcclusion(), EnergisingBlockItem::new);

    public static final EnergiserBlock ADVANCED_ENERGISER = register("advanced_energiser", EnergiserBlock::newAdvanced,
            Properties.of().requiresCorrectToolForDrops().noOcclusion(), EnergisingBlockItem::new);

    public static final EnergiserBlock REINFORCED_ENERGISER = register("reinforced_energiser", EnergiserBlock::newReinforced,
            Properties.of().requiresCorrectToolForDrops().noOcclusion(), EnergisingBlockItem::new);

    public static final ElectricalConnectorBlock ELECTRICAL_CONNECTOR = register("electrical_connector", ElectricalConnectorBlock::new,
            Properties.of().noOcclusion().strength(0.4f));

    public static final GeneratorCoilBlock GENERATOR_COIL = register("generator_coil", GeneratorCoilBlock::new,
            Properties.of().requiresCorrectToolForDrops().noOcclusion());

    public static final ImplementedMagnetBlock MAGNETITE_BLOCK = register("magnetite_block", ImplementedMagnetBlock.simple(1),
            Properties.of().requiresCorrectToolForDrops());

    public static final ImplementedMagnetBlock REDSTONE_MAGNET = register("redstone_magnet", ImplementedMagnetBlock.simple(2),
            Properties.of().requiresCorrectToolForDrops());

    public static final ImplementedMagnetBlock LAYERED_MAGNET = register("layered_magnet", ImplementedMagnetBlock.simple(4),
            Properties.of().requiresCorrectToolForDrops());

    public static final ImplementedMagnetBlock FLUXUATED_MAGNETITE = register("fluxuated_magnetite", ImplementedMagnetBlock.simple(8),
            Properties.of().requiresCorrectToolForDrops());

    public static final ImplementedMagnetBlock NETHERITE_MAGNET = register("netherite_magnet", ImplementedMagnetBlock.simple(24),
            Properties.of().requiresCorrectToolForDrops());

    public static final CarbonBrushesBlock CARBON_BRUSHES = register("carbon_brushes", CarbonBrushesBlock::new,
            Properties.of().requiresCorrectToolForDrops().noOcclusion(), CarbonBrushesItem::new);

    // The block entity types are read lazily: CNABlockEntityTypes lists these blocks as its valid
    // blocks, so it must not be initialised while this class is still being initialised.
    public static final MotorBlock BASIC_MOTOR = register("basic_motor",
            p -> new MotorBlock(p, () -> CNABlockEntityTypes.BASIC_MOTOR, new BasicMotorVariant()),
            Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(3.0f), MotorBlockItem::new);

    public static final MotorBlock ADVANCED_MOTOR = register("advanced_motor",
            p -> new MotorBlock(p, () -> CNABlockEntityTypes.ADVANCED_MOTOR, new AdvancedMotorVariant()),
            Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(3.5f), MotorBlockItem::new);

    public static final MotorBlock REINFORCED_MOTOR = register("reinforced_motor",
            p -> new MotorBlock(p, () -> CNABlockEntityTypes.REINFORCED_MOTOR, new ReinforcedMotorVariant()),
            Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(4.0f), MotorBlockItem::new);

    public static final MotorExtensionBlock BASIC_MOTOR_EXTENSION = register("basic_motor_extension",
            p -> new MotorExtensionBlock(p, () -> CNABlockEntityTypes.BASIC_MOTOR_EXTENSION, new BasicMotorExtensionVariant()),
            Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(4.0f));

    public static final MotorExtensionBlock ADVANCED_MOTOR_EXTENSION = register("advanced_motor_extension",
            p -> new MotorExtensionBlock(p, () -> CNABlockEntityTypes.ADVANCED_MOTOR_EXTENSION, new AdvancedMotorExtensionVariant()),
            Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(4.0f));

    public static final CasingBlock HEAT_CASING = register("heat_casing", CasingBlock::new,
            Properties.ofFullCopy(AllBlocks.ANDESITE_CASING).requiresCorrectToolForDrops().mapColor(MapColor.COLOR_GRAY).sound(SoundType.NETHERITE_BLOCK));

    public static final HeatPipeBlock HEAT_PIPE = register("heat_pipe", HeatPipeBlock::new,
            Properties.of().noOcclusion().requiresCorrectToolForDrops().strength(1.6f));

    public static final EncasedHeatPipeBlock ENCASED_HEAT_PIPE = registerWithoutItem("encased_heat_pipe",
            p -> new EncasedHeatPipeBlock(p, () -> CNABlocks.HEAT_CASING),
            Properties.ofFullCopy(HEAT_CASING));

    public static final HeatPumpBlock HEAT_PUMP = register("heat_pump", HeatPumpBlock::new,
            Properties.of().noOcclusion().requiresCorrectToolForDrops().strength(1.6f));

    public static final HeaterBlock HEATER = register("heater", HeaterBlock::new,
            Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(2.0f));

    public static final StirlingEngineBlock STIRLING_ENGINE = register("stirling_engine",
            p -> new StirlingEngineBlock(p, () -> CNABlockEntityTypes.STIRLING_ENGINE),
            Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(2.0f), StirlingEngineItem::new);

    public static final SolidCoriumBlock SOLID_CORIUM = register("solid_corium", SolidCoriumBlock::new,
            Properties.of().requiresCorrectToolForDrops().strength(50.0f));

    public static final CoriumBlock CORIUM = register("corium", CoriumBlock::new,
            Properties.of().requiresCorrectToolForDrops().strength(70.0f));

    public static final ReactorCasingBlock REACTOR_CASING = register("reactor_casing", ReactorCasingBlock::new,
            Properties.of().requiresCorrectToolForDrops());

    public static final ReactorEncasedHeatPipeBlock REACTOR_ENCASED_HEAT_PIPE = registerWithoutItem("reactor_encased_heat_pipe",
            p -> new ReactorEncasedHeatPipeBlock(p, () -> CNABlocks.REACTOR_CASING),
            Properties.ofFullCopy(REACTOR_CASING));

    public static final ReactorRodBlock REACTOR_ROD = register("reactor_rod", ReactorRodBlock::new,
            Properties.of().requiresCorrectToolForDrops().noOcclusion());

    public static final ReactorTransparentBlock REACTOR_GLASS = register("reactor_glass", ReactorTransparentBlock::new,
            Properties.ofFullCopy(Blocks.GLASS).isViewBlocking((state, level, pos) -> false).requiresCorrectToolForDrops());

    public static final ReactorFuelAcceptorBlock REACTOR_FUEL_ACCEPTOR = register("reactor_fuel_acceptor", ReactorFuelAcceptorBlock::new,
            Properties.of().requiresCorrectToolForDrops().noOcclusion());

    public static final ReactorHeatVentBlock REACTOR_HEAT_VENT = register("reactor_heat_vent", ReactorHeatVentBlock::new,
            Properties.of().requiresCorrectToolForDrops().noOcclusion());

    public static final SolarHeatingPlateBlock BASIC_SOLAR_HEATING_PLATE = register("basic_solar_heating_plate", SolarHeatingPlateBlock::createBasic,
            Properties.of().requiresCorrectToolForDrops().noOcclusion());

    public static final SolarHeatingPlateBlock ADVANCED_SOLAR_HEATING_PLATE = register("advanced_solar_heating_plate", SolarHeatingPlateBlock::createAdvanced,
            Properties.of().requiresCorrectToolForDrops().noOcclusion());

    public static final Block THORIUM_ORE = register("thorium_ore", Block::new,
            Properties.of().strength(3.5f).requiresCorrectToolForDrops(), AssemblyOperatorBlockItem::new);

    public static final RotatedPillarBlock COPPER_WIRE_BLOCK = register("copper_wire_block", RotatedPillarBlock::new,
            Properties.ofFullCopy(Blocks.COPPER_BLOCK.weathering().unaffected()).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.COPPER));

    public static final RotatedPillarBlock OVERCHARGED_IRON_WIRE_BLOCK = register("overcharged_iron_wire_block", RotatedPillarBlock::new,
            Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.COPPER));

    public static final RotatedPillarBlock OVERCHARGED_GOLDEN_WIRE_BLOCK = register("overcharged_golden_wire_block", RotatedPillarBlock::new,
            Properties.ofFullCopy(Blocks.GOLD_BLOCK).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.COPPER));

    public static final RotatedPillarBlock OVERCHARGED_DIAMOND_WIRE_BLOCK = register("overcharged_diamond_wire_block", RotatedPillarBlock::new,
            Properties.ofFullCopy(Blocks.DIAMOND_BLOCK).requiresCorrectToolForDrops().noOcclusion().sound(SoundType.COPPER));

    public static final StreetLightBlock STREET_LIGHT = register("street_light", StreetLightBlock::new,
            Properties.ofFullCopy(Blocks.REDSTONE_LAMP).lightLevel(s -> s.getValue(StreetLightBlock.LIGHT_LEVEL))
                    .requiresCorrectToolForDrops().noOcclusion().sound(SoundType.LANTERN));

    // Registrate's SharedProperties.softMetal() was a copy of the gold block.
    public static final LampPostBlock LAMP_POST = register("lamp_post", LampPostBlock::new,
            Properties.ofFullCopy(Blocks.GOLD_BLOCK).mapColor(MapColor.COLOR_GRAY).sound(SoundType.NETHERITE_BLOCK)
                    .requiresCorrectToolForDrops().noOcclusion());

    /**
     * What Registrate did in its {@code onRegister} and {@code transform} callbacks, now that the
     * blocks exist. Called once from {@link CreateNewAge#initialize}.
     */
    static void init() {
        impact(BASIC_ENERGISER, 4.0);
        impact(ADVANCED_ENERGISER, 8.0);
        impact(REINFORCED_ENERGISER, 32.0);
        impact(GENERATOR_COIL, 24.0);
        capacity(STIRLING_ENGINE, 32.0);

        EncasingRegistry.addVariant(HEAT_PIPE, ENCASED_HEAT_PIPE);
        EncasingRegistry.addVariant(HEAT_PIPE, REACTOR_ENCASED_HEAT_PIPE);
    }

    private static void impact(Block block, double value) {
        BlockStressValues.IMPACTS.register(block, () -> value);
    }

    private static void capacity(Block block, double value) {
        BlockStressValues.CAPACITIES.register(block, () -> value);
    }

    private static <B extends Block> B register(String name, Function<Properties, B> factory, Properties properties) {
        return register(name, factory, properties, BlockItem::new);
    }

    private static <B extends Block> B register(String name, Function<Properties, B> factory, Properties properties,
                                                BiFunction<Block, Item.Properties, ? extends Item> itemFactory) {
        B block = registerWithoutItem(name, factory, properties);
        CNAItems.registerBlockItem(block, itemFactory);
        return block;
    }

    private static <B extends Block> B registerWithoutItem(String name, Function<Properties, B> factory, Properties properties) {
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(CreateNewAge.MOD_ID, name));
        return Registry.register(BuiltInRegistries.BLOCK, key, factory.apply(properties.setId(key)));
    }
}
